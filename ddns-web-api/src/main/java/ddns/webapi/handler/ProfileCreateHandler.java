package ddns.webapi.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.client.daemon.DaemonClient;
import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IPVersion;
import ddns.client.domain.Profile;
import ddns.client.domain.dns.DnsDiscoveryInfo;
import ddns.client.domain.http.HttpDiscoveryInfo;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.IPLocation;
import ddns.client.domain.http.LocationType;
import ddns.client.domain.stun.StunDiscoveryInfo;
import ddns.client.domain.stun.StunServerInfo;
import ddns.client.profile.ProfileClient;
import ddns.webapi.session.UserSessionHolder;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ProfileCreateHandler extends HandlerBase implements HttpRequestHandler {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final ProfileClient profileClient;
    private final DaemonClient daemonClient;
    private final String basePath;

    @SneakyThrows
    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            get(response);
        } else if ("POST".equalsIgnoreCase(method)) {
            post(request, response);
        }
    }

    private void get(ClassicHttpResponse response) throws IOException, TemplateException {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Add profile");

        Template layoutTemplate = freemarkerConfig.getTemplate("layout.ftl");
        Template profileCreateTemplate = freemarkerConfig.getTemplate("profile_create.ftl");

        StringWriter profileCreateContent = new StringWriter();
        profileCreateTemplate.process(data, profileCreateContent);

        data.put("body", (TemplateDirectiveModel) (env, params, loopVars, body) -> env.getOut().write(profileCreateContent.toString()));

        StringWriter writer = new StringWriter();
        layoutTemplate.process(data, writer);
        response.setEntity(new BasicHttpEntity(new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)), ContentType.TEXT_HTML));
    }

    @SneakyThrows
    private void post(ClassicHttpRequest request, ClassicHttpResponse response) {
        String cookie = UserSessionHolder.getSessionIdFromCookies(request.getHeader("cookie").getValue());
        if (cookie == null) {
            response.setCode(302); // HTTP 302 Found (редирект)
            response.addHeader("Location", "/login");
            return;
        }

        Profile profile = new Profile();

        String line = new BufferedReader(new InputStreamReader(request.getEntity().getContent())).readLine();
        line = URLDecoder.decode(line, "UTF-8");
        Map<String, List<String>> form = parseForm(line);
        String name = getFirstValue(form, "name");
        IPVersion ipVersion = IPVersion.valueOf(getFirstValue(form, "ipVersion"));
        boolean active = getFirstValue(form, "active") != null && "on".equals(getFirstValue(form, "active"));

        profile.setName(name);
        profile.setIpVersion(ipVersion);
        profile.setActive(active);

        DiscoveryMethod discoveryMethod = DiscoveryMethod.valueOf(getFirstValue(form, "discoveryMethod"));
        switch (discoveryMethod) {
            case STUN -> profile.setStunDiscoveryInfo(fillStunDiscoveryInfo(form));
            case HTTP -> profile.setHttpDiscoveryInfo(fillHttpDiscoveryInfo(form));
            case DNS -> profile.setDnsDiscoveryInfo(fillDnsDiscoveryInfo(form));
        }

        profile.setDiscoveryMethod(discoveryMethod);

        String provider = getFirstValue(form, "provider");
        if ("Custom".equals(provider)) {
            profile.setUpdateInfo(fillCustomAddressUpdateInfo(form));
        }

        System.out.println(gson.toJson(profile));
        profileClient.addProfile(profile, basePath);

        response.setCode(302); // HTTP 302 Found (редирект)
        response.addHeader("Location", "/dashboard?message=Profile created successfully!");
    }

    private StunDiscoveryInfo fillStunDiscoveryInfo(Map<String, List<String>> form) {
        String stunHost = getFirstValue(form, "stunHost");
        int stunPort = Integer.parseInt(getFirstValue(form, "stunPort"));
        int discoveryInterval = Integer.parseInt(getFirstValue(form, "discoveryInterval"));
        int discoverySocketTimeout = Integer.parseInt(getFirstValue(form, "discoverySocketTimeout"));
        return StunDiscoveryInfo.builder()
                .discoveryInterval(discoveryInterval)
                .socketTimeout(discoverySocketTimeout)
                .server(StunServerInfo.builder()
                        .host(stunHost)
                        .port(stunPort)
                        .build())
                .build();
    }

    private HttpDiscoveryInfo fillHttpDiscoveryInfo(Map<String, List<String>> form) {
        return HttpDiscoveryInfo.builder().build();
    }

    private DnsDiscoveryInfo fillDnsDiscoveryInfo(Map<String, List<String>> form) {
        return DnsDiscoveryInfo.builder().build();
    }

    private AddressUpdateInfo fillCustomAddressUpdateInfo(Map<String, List<String>> form) {
        AddressUpdateInfo addressUpdateInfo = new AddressUpdateInfo();
        String url = getFirstValue(form, "url");
        addressUpdateInfo.setUrl(url);
        HttpMethod httpMethod = HttpMethod.valueOf(getFirstValue(form, "method"));
        addressUpdateInfo.setMethod(httpMethod);
        int socketTimeout = Integer.parseInt(getFirstValue(form, "socketTimeout"));
        addressUpdateInfo.setSocketTimeout(socketTimeout);
        IPLocation ipLocation = IPLocation.builder()
                .locationType(LocationType.valueOf(getFirstValue(form, "ipLocationType")))
                .locationName(getFirstValue(form, "ipLocationName"))
                .build();
        addressUpdateInfo.setIpLocation(ipLocation);
        String params = getFirstValue(form, "parameters");
        if (params != null) {
            Map<String, String> paramsMap = parseToMap(params);
            addressUpdateInfo.setParameters(paramsMap);
        }

        String headers = getFirstValue(form, "headers");
        if (headers != null) {
            Map<String, String> headersMap = parseToMap(headers);
            addressUpdateInfo.setHeaders(headersMap);
        }

        return addressUpdateInfo;
    }

    private Map<String, List<String>> parseForm(String formData) {
        Map<String, List<String>> params = new HashMap<>();

        if (formData == null || formData.isEmpty()) {
            return params;
        }

        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length >= 1) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = keyValue.length > 1 ?
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";

                params.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return params;
    }

    private static String getFirstValue(Map<String, List<String>> params, String key) {
        List<String> values = params.get(key);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public static Map<String, String> parseToMap(String input) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = input.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }

        return result;
    }
}

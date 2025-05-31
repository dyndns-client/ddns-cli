package ddns.webapi.handler;

import ddns.client.domain.AddressUpdateInfo;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IP;
import ddns.client.domain.IPVersion;
import ddns.client.domain.Profile;
import ddns.client.domain.http.HttpDiscoveryInfo;
import ddns.client.domain.http.HttpMethod;
import ddns.client.domain.http.HttpServerInfo;
import ddns.client.domain.http.IPLocation;
import ddns.client.domain.http.LocationType;
import ddns.client.profile.ProfileClient;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.URIBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProfileShowHandler extends HandlerBase implements HttpRequestHandler {

    private final ProfileClient profileClient;
    private final String basePath;

    @SneakyThrows
    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        URI uri = request.getUri();
        Map<String, String> params = new URIBuilder(uri).getQueryParams().stream()
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));

        if (!params.containsKey("profileName")) {
            response.setCode(404);
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", "Profile");
        data.put("profile", profileClient.getProfile(params.get("profileName"), basePath).get());
        data.put("logs", getLogs(params.get("profileName")));

        Template layoutTemplate = freemarkerConfig.getTemplate("layout.ftl");
        Template profileShowTemplate = freemarkerConfig.getTemplate("profile_show.ftl");

        StringWriter profileShowContent = new StringWriter();
        profileShowTemplate.process(data, profileShowContent);

        data.put("body", (TemplateDirectiveModel) (env, param, loopVars, body) -> env.getOut().write(profileShowContent.toString()));

        StringWriter writer = new StringWriter();
        layoutTemplate.process(data, writer);
        response.setEntity(new BasicHttpEntity(new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)), ContentType.TEXT_HTML));
    }

//    private Profile getProfile() {
//        return Profile.builder()
//                .name("example")
//                .active(true)
//                .ipVersion(IPVersion.IPv4)
//                .discoveryMethod(DiscoveryMethod.HTTP)
//                .httpDiscoveryInfo(HttpDiscoveryInfo.builder()
//                        .server(HttpServerInfo.builder()
//                                .url("http://eth0.me")
//                                .requestInfo(HttpServerInfo.RequestInfo.builder()
//                                        .method(HttpMethod.GET)
//                                        .headers(Map.of("header1", "value1", "header2", "value2"))
//                                        .parameters(Map.of("parameter1", "value1", "parameter2", "value2"))
//                                        .build())
//                                .responseInfo(HttpServerInfo.ResponseInfo.builder()
//                                        .contentType(HttpServerInfo.ContentType.TEXT_PLAIN)
//                                        .ipLocation(IPLocation.builder()
//                                                .locationName("field")
//                                                .locationType(LocationType.HEADER)
//                                                .build())
//                                        .build())
//                                .build())
//                        .discoveryInterval(2)
//                        .socketTimeout(3000)
//                        .build())
//                .updateInfo(AddressUpdateInfo.builder()
//                        .url("http://stun-host1.stun.com/")
//                        .method(HttpMethod.GET)
//                        .headers(Map.of("header1", "value1", "header2", "value2"))
//                        .parameters(Map.of("parameter1", "value1", "parameter2", "value2"))
//                        .socketTimeout(2000)
//                        .build())
//                .lastUpdateIp(new IP("192.168.0.1"))
//                .build();
//    }

    private List<String> getLogs(String profileName) {
        return profileClient.getProfileLogs(profileName, basePath);
    }
}

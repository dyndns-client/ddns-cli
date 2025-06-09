package ddns.webapi.handler;

import com.google.gson.GsonBuilder;
import ddns.client.email.EmailService;
import ddns.client.email.EmailServiceBaseImpl;
import ddns.client.profile.ProfileClient;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class DashboardHandler extends HandlerBase implements HttpRequestHandler {

    private final EmailService emailService = new EmailServiceBaseImpl(new GsonBuilder().setPrettyPrinting().create());
    private final String basePath;
    private final ProfileClient profileClient;

    @SneakyThrows
    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        if (!isSessionIdValid(request, response)) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("title", "Dashboard");
        if (emailService.getEmailInfo(basePath).isPresent()) {
            data.put("email", emailService.getEmailInfo(basePath).get());
        }
        data.put("profiles", profileClient.getProfiles(basePath));
//        data.put("profiles", getProfiles());

        Template layoutTemplate = freemarkerConfig.getTemplate("layout.ftl");
        Template dashboardTemplate = freemarkerConfig.getTemplate("dashboard.ftl");

        StringWriter dashboardContent = new StringWriter();
        dashboardTemplate.process(data, dashboardContent);

        data.put("body", (TemplateDirectiveModel) (env, params, loopVars, body) -> env.getOut().write(dashboardContent.toString()));

        StringWriter writer = new StringWriter();
        layoutTemplate.process(data, writer);
        response.setEntity(new BasicHttpEntity(new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)), ContentType.TEXT_HTML));
    }

//    private List<Profile> getProfiles() {
//        List<Profile> profiles = new ArrayList<>();
//        profiles.add(Profile.builder()
//                .name("example")
//                .active(true)
//                .ipVersion(IPVersion.IPv4)
//                .discoveryMethod(DiscoveryMethod.STUN)
//                .stunDiscoveryInfo(StunDiscoveryInfo.builder()
//                        .discoveryInterval(2)
//                        .server(StunServerInfo.builder()
//                                .host("stun-host1")
//                                .port(1000)
//                                .build())
//                        .build())
//                .updateInfo(AddressUpdateInfo.builder()
//                        .url("http://stun-host1.stun.com/")
//                        .method(HttpMethod.GET)
//                        .headers(Map.of("header1", "value1", "header2", "value2"))
//                        .parameters(Map.of("parameter1", "value1", "parameter2", "value2"))
//                        .build())
//                .build());
//
//        profiles.add(Profile.builder()
//                .name("asd")
//                .active(true)
//                .ipVersion(IPVersion.IPv4)
//                .discoveryMethod(DiscoveryMethod.STUN)
//                .stunDiscoveryInfo(StunDiscoveryInfo.builder()
//                        .discoveryInterval(2)
//                        .server(StunServerInfo.builder()
//                                .host("stun-host1")
//                                .port(1000)
//                                .build())
//                        .build())
//                .updateInfo(AddressUpdateInfo.builder()
//                        .url("http://stun-host1.stun.com/")
//                        .method(HttpMethod.GET)
//                        .headers(Map.of("header1", "value1", "header2", "value2"))
//                        .parameters(Map.of("parameter1", "value1", "parameter2", "value2"))
//                        .socketTimeout(2000)
//                        .build())
//                .lastUpdateIp(new IP("192.168.0.1"))
//                .build());
//
//        profiles.add(Profile.builder()
//                .name("234234234")
//                .active(true)
//                .ipVersion(IPVersion.IPv4)
//                .discoveryMethod(DiscoveryMethod.STUN)
//                .stunDiscoveryInfo(StunDiscoveryInfo.builder()
//                        .discoveryInterval(2)
//                        .server(StunServerInfo.builder()
//                                .host("stun-host1")
//                                .port(1000)
//                                .build())
//                        .build())
//                .updateInfo(AddressUpdateInfo.builder()
//                        .url("http://stun-host1.stun.com/")
//                        .method(HttpMethod.GET)
//                        .headers(Map.of("header1", "value1", "header2", "value2"))
//                        .parameters(Map.of("parameter1", "value1", "parameter2", "value2"))
//                        .socketTimeout(2000)
//                        .build())
//                .build());
//
//        return profiles;
//    }
}

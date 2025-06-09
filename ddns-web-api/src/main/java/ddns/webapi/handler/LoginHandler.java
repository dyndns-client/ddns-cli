package ddns.webapi.handler;

import ddns.client.auth.AuthService;
import ddns.client.auth.AuthServiceImpl;
import ddns.client.domain.Auth;
import ddns.webapi.session.UserSessionHolder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpRequestHandler;
import org.apache.hc.core5.http.io.entity.BasicHttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
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
public class LoginHandler extends HandlerBase implements HttpRequestHandler {

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
        Template loginTemplate = freemarkerConfig.getTemplate("login.ftl");
        StringWriter writer = new StringWriter();
        loginTemplate.process(new HashMap<>(), writer);
        response.setEntity(new BasicHttpEntity(new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)), ContentType.TEXT_HTML));
    }

    private void post(ClassicHttpRequest request, ClassicHttpResponse response) throws IOException, TemplateException {
        AuthService authService = new AuthServiceImpl();
        Auth auth = authService.getAuth(basePath).get();

        String line = new BufferedReader(new InputStreamReader(request.getEntity().getContent())).readLine();
        String username = getFirstValue(parseForm(line), "username");
        String password = getFirstValue(parseForm(line), "password");
        if (auth.getUsername().equals(username) && auth.getPassword().equals(password)) {
            String sessionId = UserSessionHolder.generateAndSaveSessionId();
            response.addHeader("Set-Cookie",
                    "sessionId=" + sessionId + "; Path=/; HttpOnly; Max-Age=" + auth.getSessionDuration());
            response.setCode(302); // HTTP 302 Found (редирект)
            response.addHeader("Location", "/dashboard");
        } else {
            response.setCode(401);
            response.setEntity(new StringEntity("Login failed", ContentType.TEXT_PLAIN));
        }
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
}

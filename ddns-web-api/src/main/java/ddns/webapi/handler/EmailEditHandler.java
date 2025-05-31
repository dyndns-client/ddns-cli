package ddns.webapi.handler;

import com.google.gson.GsonBuilder;
import ddns.client.domain.email.EmailInfo;
import ddns.client.email.EmailService;
import ddns.client.email.EmailServiceBaseImpl;
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
import org.apache.hc.core5.http.ProtocolException;
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
import java.util.Optional;
import java.util.Properties;

@RequiredArgsConstructor
public class EmailEditHandler extends HandlerBase implements HttpRequestHandler {

    private final EmailService emailService = new EmailServiceBaseImpl(new GsonBuilder().setPrettyPrinting().create());
    private final String basePath;

    @SneakyThrows
    @Override
    public void handle(ClassicHttpRequest request, ClassicHttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        String cookie = UserSessionHolder.getSessionIdFromCookies(request.getHeader("cookie").getValue());
        if (cookie == null) {
            response.setCode(302); // HTTP 302 Found (редирект)
            response.addHeader("Location", "/login");
            return;
        }
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            get(response);
        } else if ("POST".equalsIgnoreCase(method)) {
            post(request, response);
        }
    }

    private void get(ClassicHttpResponse response) throws IOException, TemplateException {
        Optional<EmailInfo> emailInfo = emailService.getEmailInfo(basePath);
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Email");
        emailInfo.ifPresent(info -> data.put("email", info));

        Template layoutTemplate = freemarkerConfig.getTemplate("layout.ftl");
        Template emailEditTemplate = freemarkerConfig.getTemplate("email_edit.ftl");

        StringWriter emailEditContent = new StringWriter();
        emailEditTemplate.process(data, emailEditContent);
        data.put("body", (TemplateDirectiveModel) (env, params, loopVars, body) -> env.getOut().write(emailEditContent.toString()));

        StringWriter writer = new StringWriter();
        layoutTemplate.process(data, writer);
        response.setEntity(new BasicHttpEntity(new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8)), ContentType.TEXT_HTML));
    }

    private void post(ClassicHttpRequest request, ClassicHttpResponse response) throws ProtocolException, IOException {
        String line = new BufferedReader(new InputStreamReader(request.getEntity().getContent())).readLine();
        line = URLDecoder.decode(line, "UTF-8");

        Map<String, List<String>> form = parseForm(line);
        String from = getFirstValue(form, "from");
        String to = getFirstValue(form, "to");
        boolean useSsl = getFirstValue(form, "useSsl") != null;
        boolean useStarttls = getFirstValue(form, "useStarttls") != null;
        String subject = getFirstValue(form, "subject");
        String password = getFirstValue(form, "password");
        String smtpHost = getFirstValue(form, "smtpHost");
        int smtpPort = Integer.parseInt(getFirstValue(form, "smtpPort"));

        EmailInfo emailInfo = new EmailInfo();
        emailInfo.setFrom(from);
        emailInfo.setTo(to);
        emailInfo.setSubject(subject);
        emailInfo.setPassword(password);

        Properties smtpProps = new Properties();

        if (useSsl || useStarttls) {
            smtpProps.put("mail.smtp.auth", "true");
        }
        if (useSsl) {
            smtpProps.put("mail.smtp.ssl.enable", "true");
        }
        if (useStarttls) {
            smtpProps.put("mail.smtp.starttls.enable", "true");
        }
        smtpProps.put("mail.smtp.host", smtpHost);
        smtpProps.put("mail.smtp.port", smtpPort);

        emailInfo.setSmtpProps(smtpProps);

        emailService.saveEmailInfo(basePath, emailInfo);
        response.setCode(302); // HTTP 302 Found (редирект)
        response.addHeader("Location", "/dashboard");
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

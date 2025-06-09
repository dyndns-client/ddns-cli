package ddns.webapi.handler;

import ddns.webapi.session.UserSessionHolder;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;

public class HandlerBase {
    protected final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);

    {
        freemarkerConfig.setTemplateLoader(new ClassTemplateLoader(getClass(), "/templates"));
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

    @SneakyThrows
    protected boolean isSessionIdValid(ClassicHttpRequest request, ClassicHttpResponse response) {
        String cookie = UserSessionHolder.getSessionIdFromCookies(request.getHeader("cookie").getValue());
        if (cookie == null) {
            response.setCode(302); // HTTP 302 Found (редирект)
            response.addHeader("Location", "/login");
            return false;
        }

        if (!UserSessionHolder.checkContainsSessionId(cookie)) {
            response.setCode(401);
            response.addHeader("Location", "/login");
            return false;
        }
        return true;
    }
}

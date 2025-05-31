package ddns.webapi.handler;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class HandlerBase {
    protected final Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);

    {
        freemarkerConfig.setTemplateLoader(new ClassTemplateLoader(getClass(), "/templates"));
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }
}

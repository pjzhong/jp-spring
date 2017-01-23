package jp.spring.web.util;

import jp.spring.ioc.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * Created by Administrator on 1/23/2017.
 */
public class WebUtil {

    public static WebApplicationContext getWebApplication(ServletContext context) {
        return (WebApplicationContext)context.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }
}

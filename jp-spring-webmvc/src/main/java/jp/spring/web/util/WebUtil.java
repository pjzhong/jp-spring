package jp.spring.web.util;

import jp.spring.ioc.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/23/2017.
 */
public class WebUtil {

    public static WebApplicationContext getWebApplication(ServletContext context) {
        return (WebApplicationContext)context.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    public static void sendError(int code, String message, HttpServletResponse response) {
        try {
            response.sendError(code, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

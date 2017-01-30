package jp.spring.web.util;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.web.context.ProcessContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 1/23/2017.
 */
public class WebUtil {

    public static WebApplicationContext getWebContext() {
        return getWebContext(ProcessContext.getRequest().getServletContext());
    }

    public static WebApplicationContext getWebContext(ServletContext context) {
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

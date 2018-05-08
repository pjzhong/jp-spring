package jp.spring.web.util;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.ioc.util.StringUtils;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.support.MultiPartRequest;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 1/23/2017.
 */
public class WebUtil {

    private String SEPARATOR = ",";

    public static Map<String, String[]> getRequestParamMap(HttpServletRequest request) {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if(!StringUtils.isEmpty(paramValues) && !(paramName.startsWith("__")) ) {//Not black
                //浏览器在发送数组的收，会在名字后面加一个[](对这方面了解还不够， 可能有误)
                if(paramName.endsWith("[]")) {
                    paramName = paramName.substring(0, paramName.length() - 2);
                }
                paramMap.put(paramName, paramValues);

            }
        }

        if(request instanceof MultiPartRequest) {
            paramMap.putAll( ((MultiPartRequest)request).getMultipartParameterMap() );
        }
        return paramMap;
    }

    /**
     * 获取请求URL
     * */
    public static String getLookupPathForRequest(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if(StringUtils.isEmpty(pathInfo)) {
            pathInfo = "";
        }

        int paramStringIdx = servletPath.indexOf("?");
        if(paramStringIdx != -1) {
            servletPath = servletPath.substring(0, paramStringIdx);
        }

        String result = servletPath + pathInfo;
        if(StringUtils.isEmpty(result)) {
            result = "/";
        }
        return result;
    }

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

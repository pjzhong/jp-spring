package jp.spring.web.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/3/2017.
 */
public class UrlPathHelper {

    public final static Pattern PATTERN_PATH_VARIABLE = Pattern.compile("(\\{([^}]+)\\})");

    public String getLookupPathForRequest(HttpServletRequest request) {
        String rest = getPathWithinServletMapping(request);
        if(!"".equals(rest)) {
            return rest;
        } else {
            return getPathWithinApplication(request);
        }
    }

    public String getPathWithinServletMapping(HttpServletRequest request) {
        String pathWithinApp = getPathWithinApplication(request);
        String servletPath = getServletPath(request);
        if(pathWithinApp.startsWith(servletPath)) {
            //Normal case: URI contains servlet path;
            return pathWithinApp.substring(servletPath.length());
        } else {
            return servletPath;
        }
    }

    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);

        if(requestUri.startsWith(contextPath)) {
            //Normal case: URI contains context path;
            String path = requestUri.substring(contextPath.length());
            return path;
        } else {
            //Special case: rather unusual
            return requestUri;
        }
    }

    public String getRequestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return decodeRequestString(uri);
    }

    public String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if("/".equals(contextPath)) {
            contextPath = "";
        }
        return decodeRequestString(contextPath);
    }

    public String getServletPath(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        if(servletPath.length() > 1 && servletPath.endsWith("/")) {
            servletPath = servletPath.substring(0, servletPath.length() - 1);
        }
        return servletPath;
    }

    public String decodeRequestString(String source) {
        try {
            return URLDecoder.decode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }
}

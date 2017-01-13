package jp.spring.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface UrlHandlerMapping {

    String URL_HANDLER_MAPPING = UrlHandlerMapping.class + ".root";

    UrlMapping getUrlMapping(HttpServletRequest request);
}

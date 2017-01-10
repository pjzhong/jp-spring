package jp.spring.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface UrlHandlerMapping {

    UrlMapping getUrlMapping(HttpServletRequest request);
}

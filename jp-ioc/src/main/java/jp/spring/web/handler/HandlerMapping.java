package jp.spring.web.handler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 1/10/2017.
 */
public interface HandlerMapping {

    String DEFAULT_HANDLER_MAPPING = HandlerMapping.class + ".root";

    Handler getHandler(HttpServletRequest request, String path);
}

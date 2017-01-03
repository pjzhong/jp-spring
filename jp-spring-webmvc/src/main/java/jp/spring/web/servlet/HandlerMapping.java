package jp.spring.web.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 1/3/2017.
 */
public interface HandlerMapping {

    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
}

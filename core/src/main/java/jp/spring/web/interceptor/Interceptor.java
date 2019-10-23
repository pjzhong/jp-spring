package jp.spring.web.interceptor;

import jp.spring.web.handler.HandlerContext;

/**
 * Created by Administrator on 1/27/2017.
 */
public interface Interceptor {

  /**
   * Intercept the execution of a handler. Call before the handlers start to handle HTTP request.
   * With this method, each interceptor can decide to abort the execution chain.
   *
   * @param context the context
   * @return {@code true} if the execution chain should proceed with the next interceptor or the
   * handler itself. Else, DispatcherServlet assumes that this interceptor has already dealt with
   * the response itself.
   */
  boolean beforeHandle(HandlerContext context);

  /**
   * Intercept the execution of a handler. Called after the handler has handled the Http request
   *
   * @param context the context
   */
  void afterHandle(HandlerContext context);
}

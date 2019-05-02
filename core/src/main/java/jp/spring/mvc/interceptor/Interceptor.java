package jp.spring.mvc.interceptor;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import jp.spring.mvc.handler.Handler;

/**
 * Created by Administrator on 1/27/2017.
 */
public interface Interceptor {

  /**
   * Intercept the execution of a handler. Call before the handlers start to handle HTTP request.
   * With this method, each interceptor can decide to abort the execution chain.
   *
   * @param request current HTTP request
   * @param response current HTTP response
   * @return {@code true} if the execution chain should proceed with the next interceptor or the
   * handler itself. Else, DispatcherServlet assumes that this interceptor has already dealt with
   * the response itself.
   */
  boolean beforeHandle(FullHttpRequest request, FullHttpResponse response, Handler handler);

  /**
   * Intercept the execution of a handler. Called after the handler has handled the Http request
   *
   * @param request current HTTP request
   * @param response current HTTP response
   */
  void afterHandle(FullHttpRequest request, FullHttpResponse response, Handler handle);
}

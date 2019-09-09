package com.jp.interceptors;


import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import jp.spring.web.annotation.Intercept;
import jp.spring.web.handler.Handler;
import jp.spring.web.interceptor.Interceptor;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept("/end")
public class EndInterceptor implements Interceptor {

  @Override
  public boolean beforeHandle(FullHttpRequest request, FullHttpResponse response,
      Handler handler) {
    response.setStatus(HttpResponseStatus.SERVICE_UNAVAILABLE);
    return false;
  }

  @Override
  public void afterHandle(FullHttpRequest request, FullHttpResponse response,
      Handler handle) {
    System.out.println("Handler has handled " + request.uri());
  }
}

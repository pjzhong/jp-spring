package com.jp.interceptors;


import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import jp.spring.web.annotation.Intercept;
import jp.spring.web.handler.HandlerContext;
import jp.spring.web.interceptor.Interceptor;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept("/end")
public class EndInterceptor implements Interceptor {

  @Override
  public boolean beforeHandle(HandlerContext context) {
    context.getResponse().setStatus(HttpResponseStatus.SERVICE_UNAVAILABLE);
    return false;
  }

  @Override
  public void afterHandle(HandlerContext context) {
    HttpRequest request = context.getRequest();
    System.out.println("Handler has handled " + request.uri());
  }
}

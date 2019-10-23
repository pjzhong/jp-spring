package com.jp.interceptors;


import io.netty.handler.codec.http.HttpRequest;
import jp.spring.web.annotation.Intercept;
import jp.spring.web.handler.HandlerContext;
import jp.spring.web.interceptor.Interceptor;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept("/hello/**")
public class HelloInterceptor implements Interceptor {

  @Override
  public boolean beforeHandle(HandlerContext context) {
    HttpRequest request = context.getRequest();
    System.out.println(request.uri());
    return true;
  }

  @Override
  public void afterHandle(HandlerContext context) {
    HttpRequest request = context.getRequest();
    System.out.println("Handler has handled " + request.uri());
  }
}

package com.jp.interceptors;


import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import jp.spring.mvc.annotation.Intercept;
import jp.spring.mvc.handler.Handler;
import jp.spring.mvc.interceptor.Interceptor;

/**
 * Created by Administrator on 1/27/2017.
 */
@Intercept(url = "/hello/**")
public class HelloInterceptor implements Interceptor {

  @Override
  public boolean beforeHandle(FullHttpRequest request, FullHttpResponse response,
      Handler handler) {
    System.out.println(request.uri());
    return true;
  }

  @Override
  public void afterHandle(FullHttpRequest request, FullHttpResponse response,
      Handler handle) {
    System.out.println("Handler has handled " + request.uri());
  }
}
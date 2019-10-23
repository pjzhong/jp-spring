package com.web.interceptor;

import jp.spring.web.annotation.Intercept;
import jp.spring.web.handler.HandlerContext;
import jp.spring.web.interceptor.Interceptor;

@Intercept("/plusOne")
public class PlusOneInterceptor implements Interceptor {

  @Override
  public boolean beforeHandle(HandlerContext context) {
    return true;
  }

  @Override
  public void afterHandle(HandlerContext context) {
    Integer integer = (Integer) context.getResult();
    context.setResult(integer + 1);
  }
}

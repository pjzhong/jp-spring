package com.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerContext;
import org.junit.jupiter.api.Test;

class PathVariableTest extends AbstractParamTest {

  @Test
  void classLevelTest() throws Exception {
    String result = "classLevel";
    HandlerContext hc = createHandlerContext("/module/" + result);
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    assertEquals(result, handler.invoke(o, hc.getArgs()));
  }

  @Test
  void methodLevelTest() throws Exception {
    Float result = 1.0F;
    HandlerContext hc = createHandlerContext("/module/whatever/" + result);
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    assertEquals(result, handler.invoke(o, hc.getArgs()));
  }

}

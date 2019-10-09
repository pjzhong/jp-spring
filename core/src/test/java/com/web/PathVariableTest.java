package com.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
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

  @Test
  void multiPathVariables() throws Exception {
    String name = "whatever";
    int one = 1;
    double two = 1.0;
    HandlerContext hc = createHandlerContext(String.format("/module/%s/%s/%s", name, one, two));
    Handler handler = hc.getHandler();

    Map<String, Object> expected = new HashMap<>();
    expected.put("name", name);
    expected.put("one", one);
    expected.put("two", two);

    Object o = context.getBean(handler.getBeanName());
    assertEquals(expected, handler.invoke(o, hc.getArgs()));
  }

}

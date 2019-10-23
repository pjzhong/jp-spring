package com.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerContext;
import org.junit.jupiter.api.Test;

class PathVariableTest extends AbstractTest {

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
    Float number = 1.0F;
    Map<String, Object> expected = new HashMap<>();
    expected.put("number", number);
    expected.put("name", "zjp");

    HandlerContext hc = createHandlerContext("/module/whatever/" + number,
        paramBuf(Collections.singletonMap("name", "zjp")));
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    assertEquals(expected, handler.invoke(o, hc.getArgs()));
  }

  @Test
  void methodLevelErrorTest() {
    HandlerContext hc = createHandlerContext("/module/whatever/Not-a-Number",
        paramBuf(Collections.singletonMap("name", "zjp")));
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    assertThrows(IllegalArgumentException.class, () -> handler.invoke(o, hc.getArgs()));
  }

}

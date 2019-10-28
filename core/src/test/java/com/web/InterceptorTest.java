package com.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import jp.spring.web.handler.HandlerContext;
import org.junit.jupiter.api.Test;

public class InterceptorTest extends AbstractTest {

  @Test
  void plusOneTest() throws Exception {
    HandlerContext hc = createHandlerContext("/interceptor/plusOne",
        Collections.singletonMap("i", 1));
    assertEquals(2, hc.invoke(context));
  }

}

package com.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.Arrays;
import java.util.List;
import jp.spring.web.MIME;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerContext;
import org.junit.jupiter.api.Test;

class RequestHeaderTest extends AbstractTest {

  @Test
  void standardHeaderTest() throws Exception {
    HandlerContext hc = createHandlerContext("/header");
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    assertEquals(MIME.APPLICATION_X_WWW_FORM_URLENCODED.type(), handler.invoke(o, hc.getArgs()));
  }

  @Test
  void standardHeadersTest() throws Exception {
    HandlerContext hc = createHandlerContext("/headers");
    hc.getRequest().headers().set(HttpHeaderNames.HOST, "www.test.com");
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());

    List<String> expected = Arrays
        .asList(MIME.APPLICATION_X_WWW_FORM_URLENCODED.type(), "www.test.com");
    assertEquals(expected, handler.invoke(o, hc.getArgs()));
  }

  @Test
  void customHeaderTest() throws Exception {
    HandlerContext hc = createHandlerContext("/custom");
    hc.getRequest().headers().set("custom", "100");
    Handler handler = hc.getHandler();
    Object o = context.getBean(handler.getBeanName());
    assertEquals(100, handler.invoke(o, hc.getArgs()));

    hc = createHandlerContext("/custom");
    handler = hc.getHandler();
    o = context.getBean(handler.getBeanName());
    assertNull(handler.invoke(o, hc.getArgs()));
  }


}

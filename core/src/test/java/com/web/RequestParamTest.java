package com.web;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerContext;
import org.junit.jupiter.api.Test;

public class RequestParamTest extends AbstractTest {


  @Test
  void strTest() throws Exception {

    HandlerContext hc = createHandlerContext("/str",
        paramBuf(Collections.singletonMap("hello", "test")));

    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());

    assertEquals("test", result);
  }

  @Test
  void nullTest() throws Exception {
    HandlerContext hc = createHandlerContext("/str",
        new EmptyByteBuf(ByteBufAllocator.DEFAULT));

    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());

    assertNull(result);
  }

  @Test
  void objectNullTest() throws Exception {
    HandlerContext hc = createHandlerContext("/objectNull",
        paramBuf(Collections.singletonMap("o", "test")));

    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());

    assertNull(result);
  }

  @Test
  void multiTest() throws Exception {
    Map<String, Object> params = new HashMap<>();
    params.put("hello", "hello");
    params.put("age", 1234567L);
    params.put("names", Arrays.asList("zjp", "pjz", "jpz"));

    HandlerContext hc = createHandlerContext("/multiParams", paramBuf(params));
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());

    assertEquals(params, result);
  }

  @Test
  void arrayParam() throws Exception {
    Integer[] ages = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 1};

    HandlerContext hc = createHandlerContext("/arrayParams",
        paramBuf(Collections.singletonMap("ages", ages)));
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());
    assertThat(ages, is(result));

    hc = createHandlerContext("/arrayParams",
        new EmptyByteBuf(ByteBufAllocator.DEFAULT));
    handler = hc.getHandler();
    assertNull(handler.invoke(o, hc.getArgs()));
  }

  @Test
  void listParamTest() throws Exception {
    List<Double> ages = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 1.0);

    HandlerContext hc = createHandlerContext("/listParams",
        paramBuf(Collections.singletonMap("doubles", ages)));
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());
    assertThat(ages, is(result));

    hc = createHandlerContext("/listParams",
        new EmptyByteBuf(ByteBufAllocator.DEFAULT));
    handler = hc.getHandler();
    assertNull(handler.invoke(o, hc.getArgs()));
  }

  @Test
  void setParamTest() throws Exception {
    Set<Double> ages = new HashSet<>(
        Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 1.0));

    HandlerContext hc = createHandlerContext("/setParams",
        paramBuf(Collections.singletonMap("doubles", ages)));
    Handler handler = hc.getHandler();

    Object o = context.getBean(handler.getBeanName());
    Object result = handler.invoke(o, hc.getArgs());
    assertThat(ages, is(result));

    hc = createHandlerContext("/setParams",
        new EmptyByteBuf(ByteBufAllocator.DEFAULT));
    handler = hc.getHandler();
    assertNull(handler.invoke(o, hc.getArgs()));
  }

}

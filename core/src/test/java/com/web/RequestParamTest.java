package com.web;


import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import jp.spring.ApplicationContext;
import jp.spring.DefaultApplicationContext;
import jp.spring.web.MIME;
import jp.spring.web.handler.Handler;
import jp.spring.web.handler.HandlerContext;
import jp.spring.web.handler.HandlerMapping;
import jp.spring.web.handler.Router.Route;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RequestParamTest {

  private ApplicationContext context;
  private HandlerMapping mapping;

  @BeforeEach
  void beforeEach() {
    context = new DefaultApplicationContext();
    mapping = HandlerMapping.build(context.getBeanFactory());
  }

  @AfterEach
  void afterEach() throws Exception {

    context.close();
    context = null;
    mapping = null;
  }

  private ByteBuf paramBuf(Map<String, Object> params) {
    StringJoiner joiner = new StringJoiner("&");
    for (Entry<String, Object> entry : params.entrySet()) {
      String k = entry.getKey();
      Object v = entry.getValue();
      if (v instanceof Iterable) {
        ((Iterable<?>) v).forEach(c -> joiner.add(String.format("%s=%s", k, c.toString())));
      } else if (v.getClass().isArray()) {
        int size = Array.getLength(v);
        for (int i = 0; i < size; i++) {
          joiner.add(String.format("%s=%s", k, Array.get(v, i).toString()));
        }
      } else {
        joiner.add(String.format("%s=%s", k, v.toString()));
      }
    }

    return Unpooled.wrappedBuffer(joiner.toString().getBytes(StandardCharsets.UTF_8));
  }

  private HandlerContext createHandlerContext(String uri, ByteBuf buf) {
    DefaultHttpHeaders headers = new DefaultHttpHeaders();
    headers.add(HttpHeaderNames.CONTENT_TYPE, MIME.APPLICATION_X_WWW_FORM_URLENCODED.type());

    FullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, uri, buf,
        headers, EmptyHttpHeaders.INSTANCE);

    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

    Route<Handler> route = mapping.getHandler(request);

    return HandlerContext.resolve(route, request, response);
  }

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
  void collection_list_ParamTest() throws Exception {
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
  void collection_set_ParamTest() throws Exception {
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

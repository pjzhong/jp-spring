package com.web;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

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
import java.util.Map;
import java.util.Map.Entry;
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

public abstract class AbstractTest {

  ApplicationContext context;
  HandlerMapping mapping;

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


  ByteBuf paramBuf(Map<String, Object> params) {
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

  HandlerContext createHandlerContext(String uri) {
    return createHandlerContext(uri, new EmptyByteBuf(ByteBufAllocator.DEFAULT));
  }

  HandlerContext createHandlerContext(String uri, Map<String, Object> params) {
    return createHandlerContext(uri, paramBuf(params));
  }

  HandlerContext createHandlerContext(String uri, ByteBuf buf) {
    DefaultHttpHeaders headers = new DefaultHttpHeaders();
    headers.add(HttpHeaderNames.CONTENT_TYPE, MIME.APPLICATION_X_WWW_FORM_URLENCODED.type());

    FullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, uri, buf,
        headers, EmptyHttpHeaders.INSTANCE);

    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

    Route<Handler> route = mapping.getHandler(request);

    return HandlerContext.build(route, request, response);
  }

}

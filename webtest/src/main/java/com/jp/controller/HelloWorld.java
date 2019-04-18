package com.jp.controller;

import com.jp.service.OutputService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.util.Map;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.mvc.annotation.RequestMapping;

@Controller
public class HelloWorld {

  @Autowired
  private OutputService output;

  @RequestMapping(value = "/hello/{someone}")
  public DefaultFullHttpResponse hello(Map<String, String> params) {
    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    final String build = String.format("Hello, %s", params.get("someone"));
    StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
        .append("<html><head><meta charset='utf-8' /><title>")
        .append(build)
        .append("</title></head><body>\r\n")
        .append("<h1>").append(build).append("</h1>")
        .append("</body></html>\r\n");

    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();
    output.output("Hello, World");
    return response;
  }

  @RequestMapping(value = "/love/{someone}")
  public DefaultFullHttpResponse love(Map<String, String> params) {
    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    final String build = String.format("Love, %s", params.get("someone"));
    StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
        .append("<html><head><meta charset='utf-8' /><title>")
        .append(build)
        .append("</title></head><body>\r\n")
        .append("<h1>").append(build).append("</h1>")
        .append("</body></html>\r\n");

    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();
    output.output("love, " + params.get("someone"));
    return response;
  }

  @RequestMapping(value = "/nothing/**")
  public DefaultFullHttpResponse nothing(Map<String, String> params) {
    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
        .append("<html><head><meta charset='utf-8' /><title>")
        .append("Nonthing")
        .append("</title></head><body>\r\n")
        .append("<h1>Nothing</h1>")
        .append("</body></html>\r\n");

    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();
    return response;
  }

}

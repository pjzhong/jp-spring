package com.jp.controller;

import com.jp.Model.User;
import com.jp.service.OutputService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import java.util.Arrays;
import java.util.LinkedList;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.mvc.annotation.CookieValue;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.annotation.RequestParam;

@Controller
public class HelloWorld {

  @Autowired
  private OutputService output;

  @RequestMapping(value = {"/hello/{someone}", "/hi/{someone}"}, method = {RequestMethod.GET,
      RequestMethod.POST})
  public void hello(@PathVariable("someone") String who,
      @RequestHeader("User-agent") String contentType, @CookieValue("time") int cookie,
      @RequestParam("age") long age, FullHttpResponse response) {
    response.headers()
        .add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("time", "1"));
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    final String build = String.format("Hello, %s", who);
    StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
        .append("<html><head><meta charset='utf-8' /><title>")
        .append(contentType)
        .append("</title>")
        .append(" <script src=\"https://cdn.bootcss.com/jquery/1.10.0/jquery.min.js\"></script>")
        .append("</head><body>\r\n")
        .append("<h1>").append(age).append("</h1>")
        .append("<h1>").append(cookie).append("</h1>")
        .append("<h1>").append(build).append("</h1>")
        .append("</body></html>\r\n");

    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();
    output.output("Hello, World");
  }

  @RequestMapping(value = "/love/{someone}")
  public void love(@PathVariable("someone") String params,
      FullHttpResponse response) {
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    final String build = String.format("Love, %s", params);
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
    output.output("love, " + params);
  }

  @RequestMapping(value = "/nothing/**")
  public void nothing(@RequestParam("a") String a,
      @RequestParam("user") User user, FullHttpResponse response) {

    StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
        .append("<html><head><meta charset='utf-8' />").append("<title>")
        .append(a)
        .append("</title></head><body>\r\n")
        .append("<h1>Nothing</h1>")
        .append("</body></html>\r\n");

    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();

  }

  @RequestMapping(value = {"/array/{someone}"}, method = {RequestMethod.GET,
      RequestMethod.POST})
  public void array(@PathVariable("someone") String who,
      @RequestParam("age") double[] age, @RequestParam("name") LinkedList<String> name,
      FullHttpResponse response) {
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    final String build = String.format("Hello, %s", who);
    StringBuilder buf = new StringBuilder()
        .append("<!DOCTYPE html>\r\n")
        .append("<html><head><meta charset='utf-8' /><title>")
        .append("</title>")
        .append(" <script src=\"https://cdn.bootcss.com/jquery/1.10.0/jquery.min.js\"></script>")
        .append("</head><body>\r\n")
        .append("<h1>").append(Arrays.toString(age)).append("</h1>")
        .append("<h1>").append(name).append("</h1>")
        .append("<h1>").append(build).append("</h1>")
        .append("</body></html>\r\n");

    ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
    response.content().writeBytes(buffer);
    buffer.release();
  }

  @RequestMapping("/end")
  public void shouldNotBeCalled() {
    throw new UnsupportedOperationException("should be intercepted");
  }

}

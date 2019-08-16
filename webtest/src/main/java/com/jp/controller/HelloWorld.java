package com.jp.controller;

import com.jp.Model.User;
import com.jp.service.OutputService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import jp.spring.ioc.factory.annotation.Autowired;
import jp.spring.ioc.factory.annotation.Value;
import jp.spring.mvc.annotation.Controller;
import jp.spring.mvc.annotation.PathVariable;
import jp.spring.mvc.annotation.RequestHeader;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.annotation.RequestParam;
import jp.spring.mvc.annotation.ResponseBody;

@Controller
public class HelloWorld {

  @Autowired
  private OutputService output;

  @Value(value = "package.scan")
  private String scan;

  @RequestMapping(value = {"/hello/{someone}", "/hi/{someone}"}, method = {RequestMethod.GET,
      RequestMethod.POST})
  @ResponseBody
  public Object hello(@PathVariable String someone,
      @RequestHeader("host") String header,
      @RequestParam("age") Long age) {
    Map<String, Object> map = new HashMap<>();
    map.put("h", header);
    map.put("w", someone);
    map.put("a", age);
    map.put("scan", scan);
    map.put("echo", output.output("echo"));
    return map;
  }

  @RequestMapping(value = "/love/{someone}")
  public Object love(@PathVariable String someone,
      FullHttpResponse response) {
    return new Object() {
      public String w = someone;
    };
  }

  @RequestMapping(value = "/nothing/**")
  public void nothing(@RequestParam String a,
      @RequestParam User user, FullHttpResponse response) {

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
  public void array(@PathVariable String someone,
      @RequestParam double[] age, @RequestParam LinkedList<String> name,
      FullHttpResponse response) {
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");

    final String build = String.format("Hello, %s", someone);
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

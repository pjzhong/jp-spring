### jp-spring是一个轻量型的web框架
> 为了熟悉web开发和spring而做出来的，http服务用Netty实现

## Controller
```java
@Controller
public class HelloWorld {

  @RequestMapping(value = {"/hello/{someone}", "/hi/{someone}"}, method = {RequestMethod.GET,
      RequestMethod.POST})
  public void hello(@PathVariable("someone") String who,
      @RequestHeader("User-agent") String contentType, @CookieValue("time") int cookie,
      @RequestParam("age") long age, FullHttpResponse response) {
    output.output("love, " + params);
  }

  @RequestMapping(value = "/love/{someone}")
  public void love(@PathVariable("someone") String params,
      FullHttpResponse response) {
    output.output("love, " + params);
  }

  @RequestMapping(value = "/nothing/**")
  public void nothing(@RequestParam("a") String a,
      @RequestParam("user") User user, FullHttpResponse response) {
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
```

## Interceptor
```java
@Intercept(url = "/end")
public class EndInterceptor implements Interceptor {

  @Override
  public boolean beforeHandle(FullHttpRequest request, FullHttpResponse response,
      Handler handler) {
    response.setStatus(HttpResponseStatus.SERVICE_UNAVAILABLE);
    return false;//Do not Handler this request
  }

  @Override
  public void afterHandle(FullHttpRequest request, FullHttpResponse response,
      Handler handle) {
  }
}
  
  @Intercept(url = "/hello/**")
 public class HelloInterceptor implements Interceptor {
  
    @Override
    public boolean beforeHandle(FullHttpRequest request, FullHttpResponse response,
        Handler handler) {
      System.out.println(request.uri());
      return true;
    }
  
    @Override
    public void afterHandle(FullHttpRequest request, FullHttpResponse response,
        Handler handle) {
      System.out.println("Handler has handled " + request.uri());
    }
}
```
##### 使用jp-spring开发的博客系统, <a href="https://git.oschina.net/pj_zhong/jp_blog/tree/develop/">这里</a>。(内含演示地址)
**如有不足，希望你能不吝赐教。**
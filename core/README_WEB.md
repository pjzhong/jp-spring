### jp-spring是一个轻量型的web框架
> 为了熟悉web开发和spring而做出来的，http服务用Netty实现

## Controller
```java
@Controller
public class HelloWorld {

  @RequestMapping(value = {"/hello/{who}", "/hi/{who}"}, method = {RequestMethod.GET,
      RequestMethod.POST})
  public void hello(@PathVariable String who,
      @RequestParam long age, FullHttpResponse response) {
  }

  @RequestMapping(value = "/nothing/**")
  public void nothing(@RequestParam("a") String a,
      @RequestParam User user, FullHttpResponse response) {
  }

  @RequestMapping(value = {"/array/{someone}"}, method = {RequestMethod.GET,
      RequestMethod.POST})
  public void array(@PathVariable String someone,
      @RequestParam double[] age, @RequestParam LinkedList<String> name,
      FullHttpResponse response) {
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
### 启动
```java
import java.util.Scanner;

public class HttpBuilderTest {

  public static void main(String[] args) throws Throwable {
    //默认从根本目录扫描properties配置文件
    // 默认8080端口
    NettyHttpService service = NettyHttpService.builder("test").build();//
    service.start();

    Scanner scanner = new Scanner(System.in);
    scanner.nextInt();
    service.stop();
    scanner.close();
  }
}
```
### 配置文件
```properties
package.scan=com #扫描目录
```

##### 使用jp-spring开发的博客系统, <a href="https://git.oschina.net/pj_zhong/jp_blog/tree/develop/">这里</a>。(内含演示地址)
**如有不足，希望你能不吝赐教。**
webmvc 模块使用说明
`======================`
依赖：jp-ioc(核心模块) //主要负责bean的创建和注入
      fastJson
      commons-io
      commons-fileupload
<hr>
首先创建一个Controller吧
```java
@Controller
public class ControllerExample {

}
```
这样就可以了，一个非常最简单的控制器，虽然什么方法都没有。
但这样没什么用吧，那我们加个方法并告诉Controller需要处理哪些请求
```java
@Controller
@RequestMapping("/example") //Class级别只支持配置一个路径
public class ControllerExample {
      
     @RequestMapping(value = "/one", method = RequestMethod.GET) 
     public String exmapleOne() {
        return "one";
     }
     
     @RequestMapping(value = "/two", method = RequestMethod.POST) 
     public String exmapleTwo() {
         return "two";
     }
}
```
方法结果处理：
 情况1：如果方法标记了@ResponseBody或者没有标记@ResponseBody并且返回值不是String类型的，都会转换成json格式返回客户端
 情况2：没有标记@ResponseBody并且返回值是String类型，那么系统就会认为这个返回值是一个页面，并将此页面返回到客户端
**Class级别的@RequestMapping不是必须的。**


**下面这样写是不允许的， @RequestMapping的value 属性不能为空**
虽然编译的时候不会出错，但如果访问 /example 会得到404
```java
@Controller
@RequestMapping("/example")
public class ControllerExample {
      
     @RequestMapping(method = RequestMethod.GET) 
     public String exmapleOne() {
        return "one";
     } 
}
```
<hr>
**获取请求中的参数**
```java
@Controller
@RequestMapping("/example")
public class ControllerExample {
      
     @RequestMapping(value = "/one", method = RequestMethod.GET) 
     public String exmapleOne(@RequestParam("abc") Integer one) {
        return "one";
     }
     
     @RequestMapping(value = "/two", method = RequestMethod.POST) 
     public String exmapleTwo() {
         return "two";
     }
}
```
如果你想获取请求中的参数可以使用 @RequestParam 来获取， 目前必须在 @RequestParam里面明确说明参数的名字
不然获取不到。

还有 @CookieValue, @RequestHeader可以让从Cookie或者Header里面获取数据，使用方法和@RequestParam一样。
<hr>
**@PatVariable的用法**
```java
@Controller
@RequestMapping("/example")
public class ControllerExample {
      
     @RequestMapping(value = "/one/{abc}", method = RequestMethod.GET) 
     public String exmapleOne(@PatVariable("abc") Integer one) {
        return "one";
     }
}
```
目前@PathVariable只能作用于Method级别的@RequestMapping, 对类级别无效。
并且要明确声明变量的名字， 否则无法识别

<hr>
**Interceptor**
目前Interceptor仅支持路径匹配

路径映射:以开“/”开头和“/*”结尾的路径映射，中间可以用“*”表示[a_zA-Z_0-9]的字符

**正确例子：**
 1.“/\*” ——匹配所有url
 2.“/example/\*” —— 匹配所有以“/example”开头的url
 3.“/example/test\*/hi/*” -- 匹配以下：
    “/example/test123/hi”
    “/example/testasdfdf/hi”
    “/example/testf5f5f5f/hi”
    “/example/test1213/hi”
    .......
 4.“/\*/test/hi/*” ——匹配以下
     “/123/hi/...”
     “/asd/hi/....”
     不匹配
     “/asdf/123/hi/....”
     “/123/asdf/hi/.....”
     
**错误例子：**
    1.“example/\*” —— Reject
    2.“/example”  —— Reject
    3.“/example/\*.html” —— Reject

**代码演示**
```java
@Intercept(url = "/example/test*/*")
public class TestInterceptor implements Interceptor {

    @Override
    public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println(request.getRequestURI());
        return true;
    }

    @Override
    public void afterHandler(HttpServletRequest request, HttpServletResponse response, Object handle) {
        System.out.println("Handler has handled " + request.getRequestURI());
    }
}

@Intercept(url = "/example/*")
public class TestInterceptor2 implements Interceptor {

    @Override
    public boolean beforeHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("I am test2 before");
        return true;
    }

    @Override
    public void afterHandler(HttpServletRequest request, HttpServletResponse response, Object handle) {
        System.out.println("I am test2 after");
    }
}

```

接下来要实现文件上传(2016-1-28)

<hr>

**如有不足，希望你能不吝赐教。**
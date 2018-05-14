webmvc 模块使用说明
`======================`
依赖：jp-ioc(核心模块) //主要负责bean的创建和注入
      fastJson
      commons-io
      commons-fileupload
      
## Requirements

* java 6.0+  
* tomcat 7.0+     
      
<hr>
## 首先创建一个Controller吧

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
控制器返回结果处理策略：
 情况1：如果方法标记了@ResponseBody或者没有标记@ResponseBody并且返回值不是String类型的，都会转换成json格式返回客户端
 情况2：没有标记@ResponseBody并且返回值是String类型，那么系统就会认为这个返回值是一个页面路径，并将此路径所代表的页面返回到客户端
_Class级别的@RequestMapping不是必须的。_
(2017-2-1更新)
~~下面这样写是不允许的， @RequestMapping的value 属性不能为空~~
下面这种写法的话，exampleOne()就会变成首页了

```java
@Controller
public class ControllerExample {
      
     @RequestMapping(method = RequestMethod.GET) 
     public String exmapleOne() {
        return "one";
     } 
}
```
<hr>
## @RequestParam() 获取请求中的参数

```java
@Controller
@RequestMapping("/example")
public class TestController {

    @Autowired
    OutputService outputService;

    @RequestMapping(value = "/test{one}/hi", method = RequestMethod.GET)
    public String test2(@PathVariable("one") Integer one, User user, @RequestParam("number") Float[] number) {
        System.out.println(outputService);
        outputService.output(one);
        outputService.output(user);
        outputService.output(number);
        return "test";
    }
}
```
目前@RequestParam 暂时只支持基本类型:int, Integer ,float, Float.....(Array or Collection is OK), 
但必须在 @RequestParam里面明确声明参数的名字，不然获取不到。

POJO也可自动注入(如果是多层对象可能会出错)。只要请求的参数中有对应的字段。那么jp-spring为你自动创建一个对应的对象，为将其自动注入。

还有 @CookieValue, @RequestHeader可以从Cookie或者Header里面获取数据，使用方法和@RequestParam一样。
<hr>
## @PatVariable的用法

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
目前@PathVariable只能作用于Method级别的基本类型变量, 对类级别无效。
并且要明确声明变量的名字， 否则无法识别

<hr>
## Interceptor
目前Interceptor仅支持路径匹配

路径映射:以开“/”开头和“/*”结尾的路径映射，中间可以用“\*”表示[a_zA-Z_0-9]的字符

**正确例子：**
 * "/\*" ——匹配所有url
 * "/example/\*" —— 匹配所有以"/example"开头的url
 * "/example/test\*/hi/*" -- 匹配以下：<br/>
     "/example/test123/hi"<br/>
    "/example/testasdfdf/hi"<br/>
    "/example/testf5f5f5f/hi"<br/>
    "/example/test1213/hi"<br/>
    .......<br/>
 * "/\*/test/hi/*" ——匹配以下
     "/123/hi/..."
     "/asd/hi/...."
     不匹配
     "/asdf/123/hi/...."
     "/123/asdf/hi/....."
     
**错误例子：**
    *“example/\*” —— Reject
    *“/example”  —— Reject
    *“/example/\*.html” —— Reject

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

<hr>
## 文件上传功能已实现(2017-1-30)
文件上传使用(apache-commons-io, apache-common-fileupload)实现
只要在方法参数上面提供一个MultipartFiles对象，jp-spring就会将上传的文件全部封装到里面去

代码演示(具体例子在jp-webtest)

```java
@Controller
public class ProductController {
    @RequestMapping(value = "/products/create", method = RequestMethod.GET)
    public String create() {
        return "products_create";
    }

    @RequestMapping(value = "/products/create", method = RequestMethod.POST)
    public Product create(Product product, MultipartFiles files) {
        for(MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
        }
        System.out.println(product);
        return product;
    }
}
```
具体代码请看
_DefaultMultipartResolver
MultiPartRequest
MultiPartFile
MultipartFiles_
<hr>

**不足之处**
1.参数注入不完善，对外依赖很强(fastJson)并且对数据的处理方式也不完善，导致经常注入失败
**如有不足，希望你能不吝赐教。**
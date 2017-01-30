### jp-spring是一个Spring-like框架
> 一些名称为了方便熟悉直接从Spring那里拿来......
> 用了那么久Spring，也该动手模仿一下来加深理理解

<hr/>
Inspired by:
  <a href="https://github.com/code4craft/tiny-spring" > tiny-spring </a>
  <a href="https://github.com/menyouping/jw">jw</a>
  <a href="https://git.oschina.net/huangyong/smart-framework">smart-framework</a>

jp-spring目前有下面5个部分
- jp-ioc (负责bean的创建和注入，下面的模块都依赖这个核心。 核心的详解请看<a href="https://github.com/code4craft/tiny-spring"> 这里 </a>)
- jp-aop (AOP模块， 负责管理和创建Aspect，并对目标类进行织入)
- jp-orm (ORM模块，现在还没完成)
- jp-webmvc (MVC模块， 负责映射Request到Controller)
- jp-webtest (所有模块在这里都会具体例子)


**核心模块ioc， 请点 <a href="https://git.oschina.net/pj_zhong/jp-spring/blob/master/jp-ioc/README.md?dir=0&filepath=jp-ioc%2FREADME.md&oid=3999b5e82cf0cf5f7ff12400bcb392e9d95dd287&sha=6db89758dd2d1e377c27c77858ead1c4f3b777f8">这里</a>**
```java
@Component("helloService")
public class HelloService {

    @Value("jdbc.driver")
    private String text;

    @Autowired
    private OutputService outputService;

    @Autowired
    @Qualifier("outService-2")
    private OutputService outputService2;

    public void helloWorld(String text) {
        outputService.output(text);
    }

    public void outPutHello(String text) {
        outputService2.output(text);
    }
}
```

**创建Controller**
详情请看 <a href="https://git.oschina.net/pj_zhong/jp-spring/tree/master/jp-spring-webmvc?dir=1&filepath=jp-spring-webmvc">这里</a> 
```java
@Controller
public class TestController {

    @Autowired
    OutputService outputService;

    public TestController() {
        System.out.println("Hello I am TestController");
    }


    @RequestMapping(value = "/test/{one}", method = RequestMethod.GET)
    public String test2(@PathVariable("one") Integer one, User user, @RequestParam("number") Float number) {
        System.out.println(outputService);
        outputService.output(one);
        outputService.output(user);
        outputService.output(number);
        return "test";
    }

    @RequestMapping(value = "/test456465", method = RequestMethod.POST)
    public String test() {
        return "test";
    }
}
```

**创建 Aspect**
具体介绍请看 <a href="https://git.oschina.net/pj_zhong/jp-spring/tree/master/jp-aop?dir=1&filepath=jp-aop">AOP-README</a>
```java
@Aspect
@Pointcut("execution(com.jp.controller.*.*())")
public class ControllerAspect {

    private long begin;

    @Before
    public void before(TargetSource target) {
        begin = System.nanoTime();
    }

    @After
    public void after(TargetSource target) {
        System.out.println("cost:" + (System.nanoTime() - begin));
    }
}

@Aspect
@Pointcut("execution(com.jp.controller.*.test*())")
public class TestControllerAspect {

    @Before
    public void before(TargetSource target) {
       System.out.println(target.getTargetMethod() + " begin");
    }

    @After
    public void after(TargetSource target) {
        System.out.println(target.getTargetMethod() + " end");
    }
}
```

<hr/>
**配置如下**
jp-webtest
运行环境：tomcat-7.0以上， jdk1.7以上， mysql5.6.26以上
数据脚本:jp-webtest/doc


在项目根目录下创建一个properties文件，输入下面的内容(jp-webtest里面有具体例子)
```java
package.scan=com.jp
page.folder=/page
page.extension=.html
resource.folder=/resources
upload.size=5
```

1.package.scan ——代表需要扫描的包， 自动扫描子目录，如果需要配置多个，请使用 ";" 进行分割。为了方便，我直接从根目录开始扫描
2.page.folder ——页面的文件，默认从项目根目录开始
3.page.extension ——页面的扩展名(目前支持jsp和html)，默认jsp
4.resource.folder ——静态资源的文件夹，必须配置不然找不到静态资源。如果需要配置多个，请使用";" 进行分割。
5.upload.size ——  单位:MB, 最大上传文件的限制，默认4M
<hr>
**如有不足，希望你能不吝赐教。**
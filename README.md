### jp-spring是一个Spring-like MVC框架
> 一些名称为了方便熟悉直接从pring那里拿来......
> 用了那么久Spring，也该动手模仿一下来加深理理解

<hr/>
Inspired by:
  https://github.com/code4craft/tiny-spring 
  https://github.com/menyouping/jw

jp-spring目前有下面5个部分
- jp-ioc (负责bean的创建和注入，下面的模块都依赖这个核心。 核心的详解请看<a href=" https://github.com/code4craft/tiny-spring "> 这里 </a>)
- jp-aop (AOP模块， 负责管理和创建Aspect，并对目标类进行织入)
- jp-orm (ORM模块，现在还没完成)
- jp-webmvc (MVC模块， 负责映射Request到Controller)
- jp-webtest (所有模块在这里都会具体例子)


**创建Controller**
详情请看 <a href="https://git.oschina.net/pj_zhong/jp-spring/blob/master/jp-spring-webmvc/READEME.md?dir=0&filepath=jp-spring-webmvc%2FREADEME.md&oid=aa4d8c10cd15757acf404baf8542d707c9d90456&sha=6a73525cdf0582043385b33126c4430c254e8c84">这里</a> 
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

**创建 Service**
```java
public interface OutputService {

    public <T> void output(T text);
}

@Service
public class OutputServiceImple implements OutputService {

    @Override
    public <T> void output(T text) {
        System.out.println(text);
    }
}

```

**创建 Aspect**
具体介绍请看 <a href="https://git.oschina.net/pj_zhong/jp-spring/blob/master/jp-aop/READEME.md?dir=0&filepath=jp-aop%2FREADEME.md&oid=753d50f8e9bf9d34a6363c8b935d801637a7a23c&sha=523f58efd9782c9a67704d17e92805e469e59a1d">AOP-README</a>
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

目前，我还没做到完全零配置，所以还是需要一些配置
1.web.xml
```xml
<web-app>
  <context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/config/application.xml</param-value>
  </context-param>

  <listener>
    <listener-class>jp.spring.web.context.ContextLoaderListener</listener-class>
  </listener>
    
   <servlet>
       <servlet-name>DispatcherServlet</servlet-name>
       <servlet-class>jp.spring.web.servlet.DispatcherServlet</servlet-class>
       <load-on-startup>1</load-on-startup>
   </servlet> 
    <servlet-mapping>
        <servlet-name>DispatcherServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
contextConfigLocation这个名字是规定， 对应的值就是配置文件的路径， “/”就是从根路径开始
2.application.xml
```xml
<beans>
    <context:component-scan base-package="com.jp"/> /*哪个包需要扫描,子目录也会扫描。为了方面，我直接从项目根目录开始*/

    /*一个简单是视图处理，目前仅支持html和jsp*/
    <bean id="ViewResolver" class="jp.spring.web.view.DefaultViewResolver">
        <property name="folder" value="/"/> /*页面在哪个文件按*/
        <property name="extension" value=".jsp"/> /*页面的扩展名*/
    </bean>
</beans>
```

下一步我打算使用Annotation来代替XML, 然后只需配置一些属性放在 xxx.properties文件就行，例如数据库链接，需要的扫描的包，或者页面文件夹和扩展名等。
尽快实现

<hr>
如果这个项目对你有帮助，可否留下一颗星星？
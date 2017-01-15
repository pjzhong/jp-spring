### jp-spring是一个Spring-like MVC框架
> 所以很多名称都是从Spring那里拿过来的。 目前还是在完善中......

Inspired by:
  https://github.com/code4craft/tiny-spring 
  https://github.com/menyouping/jw

它目前有两部分组成
- jp-ioc  (负责bean的创建和注入，例如常用的@Autowired)
- jp-webmvc (负责Request的映射)
- jp-webtest (前面两个项目测试)


目前实现了：
通过@Controller来标记控制器，使用@RequestMapping并且可以使用@PathVariable, 目前只支持Method级别的@PathVariable
必须在标签里面输入变量名，因为我目前不知道怎么获取方法参数的名字，虽然JAVA8可以了，但还没用过...

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

TODO

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

2.application.xml
```xml
<beans>
    <context:component-scan base-package="com.jp"/>

    <bean id="ViewResolver" class="jp.spring.web.view.DefaultViewResolver">
        <property name="folder" value="/"/>
        <property name="extension" value=".jsp"/>
    </bean>
</beans>
```
启发项目:
  https://github.com/code4craft/tiny-spring
  https://github.com/menyouping/jw

这是一个模仿Spring的MVC框架。

1. 基本使用方法
web.xml配置如下
```
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


目前已经实现了：
@Autowired, @PathVarable, @RequestMapping,@RequestParam,@CookieValue。

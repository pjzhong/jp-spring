启发项目:
  https://github.com/code4craft/tiny-spring
  https://github.com/menyouping/jw

<h1>这是一个Spring-like的MVC框架。</h1>

1. web.xml
```xml
<web-app>
<context-param>
  <param-name>contextConfigLocation</param-name>
  <param-value>/config/application.xml</param-value>
</context-param>

<listener>
  <listener-class>jp.spring.web.context.ContextLoaderListener</listener-class>   </listener>

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






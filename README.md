启发项目:
  https://github.com/code4craft/tiny-spring
  https://github.com/menyouping/jw

这是一个模仿Spring的MVC框架。
1. web.xml配置
```
web-app>
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
2. 这里是列表文本


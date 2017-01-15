启发项目:
  https://github.com/code4craft/tiny-spring
  https://github.com/menyouping/jw

这是一个模仿Spring的MVC框架。

1. web.xml
web.xml配置如下
```
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

 “<context-param>”目前必须配置，
 “<param-name>”:暂时固定为：contextConfigLocation
 “<param-value>”:配置文件。（“/”表示从根目录开始）

2.application.xml
```
<beans>
    <context:component-scan base-package="com.jp"/>

    <bean id="ViewResolver" class="jp.spring.web.view.DefaultViewResolver">
        <property name="folder" value="/"/>
        <property name="extension" value=".jsp"/>
    </bean>
</beans>
```
这是使用jp-spring最基本的配置
1.第一个需要扫描的包，自动搜索子目录
2.ViewResolver是解析页面用的，目前支持html和jsp。 folder属性告诉页面都放在哪里， extension页面的扩展名。

目前已经实现了：
@Autowired, @PathVarable, @RequestMapping,@RequestParam,@CookieValue。

jp-ioc 模块使用说明
`======================`
依赖：暂无
原型和加超详细的讲解，请点<a href="https://github.com/code4craft/tiny-spring">这里</a>

<br>
<hr>
先来看看我们都能做些什么先吧
```java
public class ApplicationContextTest {
    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new ClassPathPropertiesApplicationContext("/");
        HelloService helloService = (HelloService) applicationContext.getBean("helloService");
        helloService.helloWorld();
        helloService.outPutHello("test");
    }
}

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

<hr>
**@Component, 组件**
Class只要标记上@Component并被扫描到，就会纳入ioc的管理之中。
每个@Component都会一个名字，如果用户没有提供就会取——首字母小写的class.getSimpleName() 来作为默认名字

被ioc当作@Component的还有@Controller, @Service, @Aspect, @Bean, @Repository
<hr>
**@Autowired, 自动装配**
自动装配的策略， 默认是按类型来装配的。如果存在多个默认选第一个来进行注入。
如果你想指定实现，使用@Qualifier并提供实现类的id。就像上面的例子那样。
id为"outService-2"的实现类就会自动注入到第二个 OutputService
```java
@Service("outService-2")
public class outputServiceImpl2  implements OutputService {

    @Override
    public void output(String text) {
        System.out.println("I am outService-2");
    }
}
```
目前@Autowired只支持配置在成员变量上，对于方法无效
<hr>
**@Value**
这个标记主要是用来为类注入配置中的值。
就像 HelloService 的例子中， 成员变量text， ioc会从配置文件需要合适的值并对其注入
配置文件
```java
package.scan=jp.spring
test=123456
jdbc.driver=com.mysql.jdbc.Driver
```

@Value默认注入失败也不会报错的, 如果想提示注入失败可以这样写
```java
@Component("helloService")
public class HelloService {
    @Value(value = "jdbc.driver", required = true)
    private String text;
    
    ......
}
```
目前@Value只支持配置在成员变量上
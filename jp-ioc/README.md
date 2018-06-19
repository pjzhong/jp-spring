### jp-ioc 模块使用说明

依赖：暂无
原型和加超详细的讲解，[点这里](https://github.com/code4craft/tiny-spring)。
先我们看下应该怎么做吧

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

## @Component, 组件
Class只要标记上@Component并被扫描到，就会纳入ioc的管理之中。
每个@Component都会一个名字，如果用户没有提供就会取——首字母小写的class.getSimpleName() 来作为默认名字

被ioc当作@Component的还有@Controller, @Service, @Aspect, @Bean, @Repository


## @Autowired, 自动装配
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

_目前@Autowired只支持配置在成员变量上，对于方法无效_

## @Value
这个标记主要是用来为类注入配置中的值。
就像 HelloService 的例子中， 成员变量text， ioc会从配置文件需要合适的值并对其注入

配置文件放在项目根路径下

```
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

## beanPostProcessor, 初始化和加工
(2017-1-25)在开发这个项目中，如何整合各个组件(jp-ioc, jp-web, jp-aop)这个问题一直困扰着我。
我的想法是尽可能隔离各个组件，让每个组件都只依赖于jp-ioc。 在前期开发的时候，一直没这方面的经验，
所以只好的在WebApplicationContext里面人工组合了各个组件了(下下签啊)。

在看到<a href="https://my.oschina.net/huangyong/blog/173260">smart-plugin</a> 里面提到的插件式开发。
我对此挺感兴趣的，就决定用这个方法来试下吧。

beanPostProcessor在Spring Ioc container里面是一个对外扩展的接口(在bean初始化前后做一些额外的加工)，可以让用户或者或者组件对其感兴趣的bean进行一些处理
例如 AOP对目标对象的织入。

好，那我们就以这个类为桥梁，连接各个模块。不过，我稍微改变了一下

```java
/**
 * ioc容器在初始化和创建bean对外暴露的接口
 * 其它模块如果需要对ioc注册自己的beanPostProcessor。
 * 只需要这样做：
 * 创建一个class继承beanPostProcessor标记上component，创建jp.spring.process包并放在里面。
 * 这样容器在启动的时候就会去加载。
 * */
public interface BeanPostProcessor {

    /**
     * 对某一类的bean进行特定的初始化，例如controller, aspect
     * */
    void postProcessBeforeInitialization() throws Exception;

    /**
    * 在bean初始化完成之后，进行额外加工。例如 aop的织入
    */
    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}
```
具体例子可以查看 webmvc和aop模块jp.spring.process包里面的beanPostProcessor实现


## 不足之处
1.无法处理数组和集合的注入
2.无法处理泛型

**如有不足，希望你能不吝赐教**
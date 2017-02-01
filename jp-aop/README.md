Aop 模块使用说明
`======================`
依赖：jp-ioc(核心模块)和cglib

<hr>

首先编写一个类并为其标上@Aspect和@Pointcut(这两个是必须的)， 然后在@Pointcut里面编写规则(规则介绍请看例子下方)。
被@Before和@After的方法，参数中只能有TargetSource， TargetSource包含了目标对象的基本信息，包括目标类，实例，被调用的方法和参数

@Error标记的方法只能有两个参数，其中一个是目标方法抛出的异常，(具体例子在jp-webtest模块里面)
```java
@Aspect
@Pointcut("execution(com.jp.controller.*.*())")
public class ControllerAspect {

    private long begin;

    @Before /*将在目标方法执行之前被调用*/
    public void before(TargetSource target) {
        begin = System.nanoTime();
    }

    @After /*将在目标方法被执行之后被调用/
    public void after(TargetSource target) {
        System.out.println("cost:" + (System.nanoTime() - begin));
    }
    
    @Error /*将在目标方法抛出异常之后调用*/
    public void error(Throwable e, TargetSource target) {
        System.out.println(e);
    }
}
```
<hr>
**@Pointcut规则**
@Pointcut("execution(com.jp.controller.\*.*())")

规则最核心的有两部分， 以上面的@Pointcut作为例子
  <p> 第一部分.类名规则:
   1.com.jp.controller.\* &nbsp;&nbsp;&nbsp; **com.jp.controller包下面的所有类。 ！！！注意不包含子目录**
   2.com.jp.controller.. &nbsp;&nbsp;&nbsp; **com.jp.controller包下面的所有类,包含子目录**
  </p>
  <p>                   
  第二部分.方法名规则： 
  1.\*() ,&nbsp;&nbsp;&nbsp; **任何方法。 ！！！注意， 目前只能根据方法名来进行过滤，但是方法名后面的()还是要加上**
  2.\*Service()，&nbsp;&nbsp;&nbsp; **以Service结尾的方法，都会被拦截。  ！！！注意， 目前只能根据方法名来进行过滤，但是方法名后面的()还是要加上**
  </p>                      
  然后把两部份组合起来: 
         1.com.jp.controller.* + "." + \*() = com.jp.controller.*.*(),  &nbsp;&nbsp;&nbsp;  com.jp.controller包下面的所有类的方法都会被拦截。!!!不包含子目录
         2.com.jp.controller.. + "." + \*Service() = com.jp.controller...\*Service()  &nbsp;&nbsp;&nbsp; com.jp.controller包下面的所有类的， 以Service结尾的方法都会被拦截。!!!包含子目录                

  最后这样把他们包起来
    1. execution("com.jp.controller.\*.*()")
    2. execution("com.jp.controller...\*Service()")
  放进@Pointcut就可以了。
<hr>

**如有不足，希望你能不吝赐教**
    


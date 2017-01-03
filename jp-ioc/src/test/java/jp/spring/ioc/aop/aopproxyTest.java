package jp.spring.ioc.aop;

import jp.spring.ioc.HelloService;
import jp.spring.ioc.aop.pointcut.AspectJExpressionPointcut;
import jp.spring.ioc.aop.proxy.CglibAopProxy;
import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.impl.ClassPathXmlApplicationContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;

/**
 * Created by Administrator on 12/26/2016.
 */
public class aopproxyTest {

    @Test
    public void testInterceptor() throws Exception {
        ApplicationContext applicationContext =  new ClassPathXmlApplicationContext("tinyioc.xml");
        HelloService helloService = (HelloService) applicationContext.getBean("helloWorldService");


        //Set proxying Object(Joinpoint)
        AdviseSupport adviseSupport = new AdviseSupport();
        TargetSource targetSource = new TargetSource(helloService, HelloService.class);
        adviseSupport.setTargetSource(targetSource);

        //Set interceptor(Advice)
        MethodInterceptor timer = new TimerInterceptor();
        adviseSupport.setMethodInterceptor(timer);

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

        pointcut.setExpression("execution(* jp.spring.ioc.aop.*.*())");

        adviseSupport.setMethodMatcher(pointcut);

        //create proxy
        CglibAopProxy jdkDynamicAopProxy = new CglibAopProxy(adviseSupport);
        HelloService helloWorldService = (HelloService) jdkDynamicAopProxy.getProxy();


        //Invocation base on AOP
        helloWorldService.helloWorld();


    }
}

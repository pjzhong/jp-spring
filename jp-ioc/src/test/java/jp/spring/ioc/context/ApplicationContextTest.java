package jp.spring.ioc.context;

import jp.spring.ioc.HelloService;
import jp.spring.ioc.TestController;
import jp.spring.ioc.context.impl.ClassPathPropertiesApplicationContext;
import jp.spring.ioc.context.impl.ClassPathXmlApplicationContext;
import jp.spring.ioc.zjp;
import org.junit.Test;

/**
 * Created by Administrator on 12/26/2016.
 */
public class ApplicationContextTest {

    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new ClassPathPropertiesApplicationContext("/");
        HelloService helloService = (HelloService) applicationContext.getBean("helloService");
        helloService.helloWorld();
        helloService.outPutHello("test");
    }
}

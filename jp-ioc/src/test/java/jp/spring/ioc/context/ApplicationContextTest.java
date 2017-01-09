package jp.spring.ioc.context;

import jp.spring.ioc.HelloService;
import jp.spring.ioc.TestController;
import jp.spring.ioc.context.impl.ClassPathXmlApplicationContext;
import jp.spring.ioc.zjp;
import org.junit.Test;

/**
 * Created by Administrator on 12/26/2016.
 */
public class ApplicationContextTest {

    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("tinyioc.xml");
        TestController testController = (TestController) applicationContext.getBean("testController");
        System.out.println(testController.out());
  /*      HelloService helloService = (HelloService) applicationContext.getBean("helloWorldService");
        helloService.helloWorld();*/

   /*     System.out.println();
        helloService.helloWorld();


        System.out.println();
        helloService.setApplicationContext(null);
        helloService.helloWorld();*/

      /*  zjp myzjp = (zjp) applicationContext.getBean("123456");
        myzjp.TheSimplestScan();*/
  /*      AspectJExpressionPointcutAdvisor test = new AspectJExpressionPointcutAdvisor();
        System.out.println(test.getClass().getDeclaredMethod("setExpression", "".getClass()));*/
    }
}

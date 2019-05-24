package jp.spring.ioc.simleDemo;

import jp.spring.ioc.context.ApplicationContext;
import jp.spring.ioc.context.DefaultApplicationContext;
import jp.spring.ioc.simleDemo.service.TestService;
import org.junit.Test;

public class IocTest {

  @Test
  public void test() throws Exception {
    ApplicationContext context = new DefaultApplicationContext("/");
    TestService service = (TestService) context.getBean("testServiceImple");
    service.sayHello(null);
  }
}

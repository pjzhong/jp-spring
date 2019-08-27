package jp.spring.ioc.factory.simleDemo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jp.spring.ApplicationContext;
import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.factory.simleDemo.service.TestService;
import jp.spring.ioc.factory.simleDemo.service.TestServiceImple;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;


public class IocTest {

  @Test
  public void test() throws Exception {
    ApplicationContext context = new DefaultApplicationContext();
    TestService service = (TestService) context
        .getBean(TypeUtil.determinedName(TestServiceImple.class));
    assertEquals("Hello World!", service.say("Hello World!"));
  }
}

package jp.spring.ioc.simleDemo;

import static org.junit.Assert.assertEquals;

import jp.spring.ApplicationContext;
import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.simleDemo.service.TestService;
import jp.spring.ioc.simleDemo.service.TestServiceImple;
import jp.spring.util.TypeUtil;
import org.junit.Test;

public class IocTest {

  @Test
  public void test() throws Exception {
    ApplicationContext context = new DefaultApplicationContext();
    TestService service = (TestService) context
        .getBean(TypeUtil.determinedName(TestServiceImple.class));
    assertEquals("Hello World!", service.say("Hello World!"));
  }
}

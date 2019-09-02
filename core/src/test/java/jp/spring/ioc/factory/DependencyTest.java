package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jp.spring.ApplicationContext;
import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.annotation.Component;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;


class DependencyTest {

  @Test
  void test() {
    ApplicationContext context = new DefaultApplicationContext();
    TestService service = (TestService) context
        .getBean(TypeUtil.determinedName(TestServiceImple.class));
    assertNotNull(service);
    assertEquals("Hello World!", service.say("Hello World!"));
  }

  public interface TestService {

    String say(String hi);
  }

  @Component
  public static class TestServiceImple implements TestService {

    public TestServiceImple() {
    }

    @Override
    public String say(String hi) {
      return hi;
    }
  }
}

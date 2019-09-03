package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.annotation.Component;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;


class DependencyTest {

  @Test
  void no_bean_test() {
    assertThrows(BeansException.class, () -> {
      BeanFactory factory = new DefaultApplicationContext();

      factory.getBean(TypeUtil.simpleClassName(List.class));
    });
  }

  @Test
  void register_get_test() {
    BeanFactory factory = new DefaultApplicationContext();
    factory.registerDependency(TypeUtil.simpleClassName(List.class), Collections.emptyList());
    assertEquals(Collections.emptyList(), factory.getBean(TypeUtil.simpleClassName(List.class)));
  }

  @Test
  void get_test() {
    BeanFactory context = new DefaultApplicationContext();
    TestService service = (TestService) context
        .getBean(TypeUtil.simpleClassName(TestServiceImple.class));
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

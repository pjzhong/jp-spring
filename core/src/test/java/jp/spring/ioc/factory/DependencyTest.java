package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.annotation.Named;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;


class DependencyTest {

  @Test
  void noBeanTest() {
    assertThrows(BeansException.class, () -> {
      BeanFactory factory = new DefaultApplicationContext();

      factory.getBean("No Such Bean");
    });
  }

  @Test
  void registerGetTest() {
    BeanFactory factory = new DefaultApplicationContext();
    factory.registerDependency(TypeUtil.simpleClassName(List.class), Collections.emptyList());
    assertEquals(Collections.emptyList(), factory.getBean(TypeUtil.simpleClassName(List.class)));
  }

  @Test
  void getTest() {
    BeanFactory context = new DefaultApplicationContext();
    TestService service = (TestService) context
        .getBean(TypeUtil.simpleClassName(TestServiceImple.class));
    assertNotNull(service);
    assertEquals("Hello World!", service.say("Hello World!"));
  }

  @Test
  void namedTest() {
    BeanFactory context = new DefaultApplicationContext();
    NamedClass service = (NamedClass) context
        .getBean("hello,world");
    assertNotNull(service);
    assertEquals("hello,world", service.hi());
  }

  public interface TestService {

    String say(String hi);
  }

  @Named("hello,world")
  @Component
  public static class NamedClass {

    public String hi() {
      return "hello,world";
    }
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

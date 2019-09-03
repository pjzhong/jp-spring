package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.annotation.Component;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;

class SingletonTest {

  @Test
  void singletonTest() {
    BeanFactory factory = new DefaultApplicationContext();
    SingletonObj a = (SingletonObj) factory.getBean(TypeUtil.simpleClassName(SingletonObj.class));
    SingletonObj b = (SingletonObj) factory.getBean(TypeUtil.simpleClassName(SingletonObj.class));

    assertNotNull(a);
    assertNotNull(b);
    assertEquals(a, b);
  }


  @Component
  public static class SingletonObj {

  }

}

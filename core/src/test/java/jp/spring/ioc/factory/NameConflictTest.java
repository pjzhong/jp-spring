package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;

class NameConflictTest {

  @Test
  void definition_test() {
    assertThrows(IllegalArgumentException.class, () -> {
      BeanFactory factory = new DefaultBeanFactory();

      factory.registerBeanDefinition(new BeanDefinition("A", Object.class));
      factory.registerBeanDefinition(new BeanDefinition("A", Object.class));
    });
  }

  @Test
  void object_test() {
    assertThrows(IllegalArgumentException.class, () -> {
      BeanFactory factory = new DefaultBeanFactory();

      factory.registerDependency(TypeUtil.simpleName(Object.class), new Object());
      factory.registerDependency(TypeUtil.simpleName(Object.class), new Object());
    });
  }
}
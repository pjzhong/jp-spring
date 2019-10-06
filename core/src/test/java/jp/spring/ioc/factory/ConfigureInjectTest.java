package jp.spring.ioc.factory;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.annotation.Value;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;

public class ConfigureInjectTest {

  @Test
  void injectTest() {
    BeanFactory context = new DefaultApplicationContext();
    ValueExists exists = (ValueExists) context
        .getBean(TypeUtil.resolveName(ValueExists.class));
    assertNotNull(exists);
    assertEquals("test", exists.test);
  }

  @Test
  void injectNotExistsTest() {
    BeanFactory context = new DefaultApplicationContext();
    assertThrows(BeansException.class, () -> context
        .getBean(TypeUtil.resolveName(ValueNotExists.class)));
  }

  @Test
  void injectMissTypeTest() {
    BeanFactory context = new DefaultApplicationContext();
    assertThrows(BeansException.class, () -> context
        .getBean(TypeUtil.resolveName(ValueNotExists.class)));
  }


  @Component
  public static class ValueExists {

    @Value
    public String test;
  }

  @Component
  public static class MissType {

    @Value(required = true)
    public Integer missType;
  }

  @Component
  public static class ValueNotExists {

    @Value(required = true)
    public String notExists;
  }
}

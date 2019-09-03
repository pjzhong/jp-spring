package jp.spring.ioc.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jp.spring.DefaultApplicationContext;
import jp.spring.ioc.NoUniqueBeansException;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.annotation.Named;
import jp.spring.ioc.annotation.Qualifier;
import jp.spring.util.TypeUtil;
import org.junit.jupiter.api.Test;

public class QualifierTest {

  @Test
  void NoUniqueTest() {
    assertThrows(NoUniqueBeansException.class, () -> {
      BeanFactory factory = new DefaultApplicationContext();
      factory.getBean(TypeUtil.resolveClassName(NoUnique.class));
    });
  }

  @Test
  void qualifierTest() {
    BeanFactory factory = new DefaultApplicationContext();
    WithQualifier qualifier = (WithQualifier) factory
        .getBean(TypeUtil.resolveClassName(WithQualifier.class));

    assertNotNull(qualifier);
    assertEquals(qualifier.impl.getClass(), OneImpl.class);
  }

  @Test
  void qualifierNamedTest() {
    BeanFactory factory = new DefaultApplicationContext();
    WithQualifier qualifier = (WithQualifier) factory
        .getBean(TypeUtil.resolveClassName(WithQualifier.class));

    assertNotNull(qualifier);
    assertEquals(qualifier.twoImpl.getClass(), TwoImpl.class);
  }

  interface Impl {

  }

  @Component
  public static class WithQualifier {

    @Qualifier("oneImpl")
    @Autowired
    private Impl impl;

    @Qualifier("two")
    @Autowired
    private Impl twoImpl;
  }

  @Component
  public static class NoUnique {

    @Autowired
    private Impl impl;
  }


  @Component
  public static class OneImpl implements Impl {

  }

  @Named("two")
  @Component
  public static class TwoImpl implements Impl {

  }

}

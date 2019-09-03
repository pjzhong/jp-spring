package jp.spring.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Named {

  /**
   * The name of which bean would be injected
   *
   * @since 2019年09月03日 20:01:31
   */
  String value();

}

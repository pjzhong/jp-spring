package jp.spring.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jp.spring.ioc.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Controller {

  /**
   * The value may indicate a suggestion for a logical component name, to be turned into a Spring
   * bean in case of an autodetected component.
   *
   * @return the suggested component name, if any
   */
  String value() default "";

}
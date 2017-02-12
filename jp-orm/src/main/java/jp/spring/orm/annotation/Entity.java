package jp.spring.orm.annotation;

import jp.spring.ioc.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2/12/2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Component
public @interface Entity {

    String value() default "";
}

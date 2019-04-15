package jp.spring.ioc.beans.factory.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 1/23/2017.
 * get Value from xxxx.properties
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {

    String value() default "";

    boolean required() default false;
}

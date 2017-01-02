package jp.spring.ioc.aop;

import org.aopalliance.aop.Advice;

/**
 * Created by Administrator on 12/26/2016.
 */
public interface Advisor {

    Advice getAdvice();
}

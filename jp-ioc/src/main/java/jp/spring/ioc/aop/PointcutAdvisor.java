package jp.spring.ioc.aop;

import jp.spring.ioc.aop.pointcut.Pointcut;

/**
 * Created by Administrator on 12/26/2016.
 */
public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();
}

package jp.spring.aop;

/**
 * Created by Administrator on 1/18/2017. 用于管理切面
 */
public abstract class BaseAspect {

  public abstract Pointcut getPointcut();
}

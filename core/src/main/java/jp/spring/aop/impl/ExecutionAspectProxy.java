package jp.spring.aop.impl;


import java.lang.reflect.Method;
import java.util.List;
import jp.spring.aop.BaseAspect;
import jp.spring.aop.Pointcut;
import jp.spring.aop.Proxy;
import jp.spring.aop.annotation.After;
import jp.spring.aop.annotation.Before;
import jp.spring.aop.annotation.Error;
import jp.spring.aop.support.ProxyChain;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Administrator on 1/19/2017.
 */
public class ExecutionAspectProxy extends BaseAspect implements Proxy {

  private Pointcut pointcut;

  private Object interceptor;

  private Method beforeMethod;

  private Method afterMethod;

  private Method errorMethod;

  public ExecutionAspectProxy(Class<?> aspectClass, Object aspectObject) {
    String expression = aspectClass.getAnnotation(jp.spring.aop.annotation.Pointcut.class).value();
    pointcut = new ExecutionPointcut(expression);

    interceptor = aspectObject;
    List<Method> methods = TypeUtil.findMethods(aspectClass, Before.class);

    if (ObjectUtils.isNotEmpty(methods)) {
      beforeMethod = methods.get(0);
    }

    methods = TypeUtil.findMethods(aspectClass, After.class);
    if (ObjectUtils.isNotEmpty(methods)) {
      afterMethod = methods.get(0);
    }

    methods = TypeUtil.findMethods(aspectClass, Error.class);
    if (ObjectUtils.isNotEmpty(methods)) {
      errorMethod = methods.get(0);
    }
  }

  @Override
  public void doProxy(ProxyChain proxyChain) {
    try {
      if (getPointcut().match(proxyChain.getTarget().getTargetMethod())) {
        invoke(interceptor, beforeMethod, proxyChain.getTarget());
        proxyChain.doProxyChain();
        invoke(interceptor, afterMethod, proxyChain.getTarget());
      } else {
        proxyChain.doProxyChain();
      }
    } catch (Throwable e) {
      invoke(interceptor, errorMethod, e, proxyChain.getTarget());
    }
  }

  @Override
  public Pointcut getPointcut() {
    return pointcut;
  }

  private void invoke(Object obj, Method method, Object... parameters) {
    try {
      if (obj == null && method == null) {
        return;
      }
      method.invoke(obj, parameters);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }
}

package jp.spring.aop.process;

import java.util.ArrayList;
import java.util.List;
import jp.spring.aop.BaseAspect;
import jp.spring.aop.Proxy;
import jp.spring.aop.ProxyFactory;
import jp.spring.aop.annotation.Aspect;
import jp.spring.aop.impl.ExecutionAspectProxy;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.factory.BeanDefinition;
import jp.spring.ioc.factory.BeanPostProcessor;
import jp.spring.ioc.factory.DefaultBeanFactory;
import jp.spring.util.TypeUtil;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Administrator on 1/19/2017.
 */
@Component
public class AspectBeanPostProcessor implements BeanPostProcessor {

  @Autowired
  private DefaultBeanFactory beanFactory;

  /**
   * initialize all the aspects provide by user
   */
  @Override
  public void postProcessBeforeInitialization() throws Exception {
    List<String> beanNames = beanFactory.getNamesByAnnotation(Aspect.class);

    for (String name : beanNames) {
      BaseAspect aspect = new ExecutionAspectProxy(beanFactory.getType(name),
          beanFactory.getBean(name));
      beanFactory.registerDependency(name + ".proxy", aspect);
    }
  }

  /**
   * starting weaving the target object
   */
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) {
    if (!filter(bean)) {
      return bean;
    }

    List<BaseAspect> baseAspects = beanFactory.getBeansByType(BaseAspect.class);
    List<Proxy> proxies = new ArrayList<>();
    for (BaseAspect aspect : baseAspects) {
      if (aspect.getPointcut().match(bean.getClass())) {
        if (aspect instanceof Proxy) {
          proxies.add((Proxy) aspect);
        }
      }
    }

    if (ObjectUtils.isNotEmpty(proxies)) {
      bean = ProxyFactory.getInstance().createProxy(bean, proxies);
    }

    return bean;
  }

  /**
   * class annotated by Aspect , subclass of BaseAspect and  subclass of beanPostProcessor a are not
   * the target of this processor
   */
  private boolean filter(Object bean) {
    if (TypeUtil.isAnnotated(bean.getClass(), Aspect.class)
        || bean instanceof BeanPostProcessor
        || bean instanceof BaseAspect
        || bean instanceof Proxy) {
      return false;
    }

    return true;
  }
}

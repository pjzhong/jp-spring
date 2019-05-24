package jp.spring.aop.process;

import java.util.ArrayList;
import java.util.List;
import jp.spring.aop.BaseAspect;
import jp.spring.aop.Proxy;
import jp.spring.aop.ProxyFactory;
import jp.spring.aop.impl.ExecutionAspectProxy;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.DefaultBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.stereotype.Aspect;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.TypeUtil;

/**
 * Created by Administrator on 1/19/2017.
 */
@Component
public class AspectBeanPostProcessor implements BeanPostProcessor , BeanFactoryAware{

    private DefaultBeanFactory beanFactory;

    /**
     * initialize all the aspects provide by user
     */
    @Override
    public void postProcessBeforeInitialization() throws Exception {
        List<String> beanNames = beanFactory.getBeanNamByAnnotation(Aspect.class);

        BeanDefinition beanDefinition;
        for(String name : beanNames) {
            beanDefinition = new BeanDefinition();
            BaseAspect aspect = new ExecutionAspectProxy(beanFactory.getType(name), beanFactory.getBean(name));

            beanDefinition.setClazz(aspect.getClass());
            beanDefinition.setBean(aspect);

            beanFactory.registerBeanDefinition(name + ".proxy", beanDefinition);
        }
    }

    /**
     * starting weaving the target object
     * @param  bean
     * */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
       if(!filtrate(bean, beanName)) {
           return bean;
       }

        List<BaseAspect> baseAspects = beanFactory.getBeansByType(BaseAspect.class);
        List<Proxy> proxies = new ArrayList<>();
        for(BaseAspect aspect : baseAspects) {
            if(aspect.getPointcut().match(bean.getClass())) {
                if(aspect instanceof Proxy) {
                    proxies.add((Proxy) aspect);
                }
            }
        }

        if(!TypeUtil.isEmpty(proxies)) {
            bean = ProxyFactory.getInstance().createProxy(bean, proxies);
        }

        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if(beanFactory instanceof DefaultBeanFactory) {
            this.beanFactory = (DefaultBeanFactory)beanFactory;
        }
    }

    /**
     * class annotated by Aspect , subclass of BaseAspect and  subclass of beanPostProcessor a
     * are not the target of this processor
     * */
    private boolean filtrate(Object bean, String beanName) {
        if(TypeUtil.isAnnotated(bean.getClass(), Aspect.class)
                || bean instanceof BeanPostProcessor
                || bean instanceof BaseAspect
                || bean instanceof Proxy) {
            return false;
        }

        return true;
    }
}

package jp.spring.process;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.Proxy;
import jp.spring.aop.ProxyFactory;
import jp.spring.aop.impl.ExecutionAspectProxy;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.stereotype.Aspect;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.JpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/19/2017.
 */
@Component
public class AspectBeanPostProcessor implements BeanPostProcessor , BeanFactoryAware{

    private AbstractBeanFactory beanFactory;

    public AspectBeanPostProcessor() {
        System.out.println("I am aspectBeanPostProcessor");
    }

    @Override
    public void postProcessBeforeInitialization() throws Exception {
        List<String> beanNames = beanFactory.getBeanNamByAnnotation(Aspect.class);

        BeanDefinition beanDefinition;
        for(String name : beanNames) {
            beanDefinition = new BeanDefinition();
            BaseAspect aspect = new ExecutionAspectProxy(beanFactory.getType(name), beanFactory.getBean(name));

            beanDefinition.setBeanClass(aspect.getClass());
            beanDefinition.setBean(aspect);

            beanFactory.registerBeanDefinition(name + ".proxy", beanDefinition);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
       if(!filtrate(bean, beanName)) {
           return bean;
       }

        List<BaseAspect> baseAspects = beanFactory.getBeansForType(BaseAspect.class);
        List<Proxy> proxies = new ArrayList<>();
        for(BaseAspect aspect : baseAspects) {
            if(aspect.getPointcut().match(bean.getClass())) {
                if(aspect instanceof Proxy) {
                    proxies.add((Proxy) aspect);
                }
            }
        }

        if(!JpUtils.isEmpty(proxies)) {
            bean = ProxyFactory.getInstance().createProxy(bean, proxies);
        }

        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if(beanFactory instanceof AbstractBeanFactory) {
            this.beanFactory = (AbstractBeanFactory)beanFactory;
        }
    }

    /**
     * class annotated by Aspect , subclass of BaseAspect and  subclass of beanPostProcessor a
     * are not the target of this processor
     * */
    private boolean filtrate(Object bean, String beanName) {
        if(JpUtils.isAnnotated(bean.getClass(), Aspect.class)
                || bean instanceof BeanPostProcessor
                || bean instanceof BaseAspect
                || bean instanceof Proxy) {
            return false;
        }

        return true;
    }
}

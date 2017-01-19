package jp.spring.aop.impl;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.support.AdviseSupport;
import jp.spring.aop.support.TargetSource;
import jp.spring.ioc.beans.BeanPostProcessor;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;

import java.util.List;

/**
 * Created by Administrator on 1/19/2017.
 */
public class AspectBeanPostProcessor implements BeanPostProcessor , BeanFactoryAware{

    private AbstractBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        List<BaseAspect> baseAspects = beanFactory.getBeansForType(BaseAspect.class);
        for(BaseAspect aspect : baseAspects) {
            if(aspect.match(bean.getClass())) {
                AdviseSupport support = new AdviseSupport();
                support.setInterceptor(aspect);
                support.setTargetSource(new TargetSource(bean));

                return new CglibAopProxy(support).getProxy();
            }
        }

        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        if(beanFactory instanceof AbstractBeanFactory) {
            this.beanFactory = (AbstractBeanFactory)beanFactory;
        }
    }
}

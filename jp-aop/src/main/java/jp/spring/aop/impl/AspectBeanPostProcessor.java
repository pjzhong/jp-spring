package jp.spring.aop.impl;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.Proxy;
import jp.spring.aop.ProxyFactory;
import jp.spring.ioc.beans.BeanPostProcessor;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;
import jp.spring.ioc.util.JpUtils;

import java.util.ArrayList;
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
}

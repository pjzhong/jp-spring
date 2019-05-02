package jp.spring.ioc.context.impl;

import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.context.AbstractApplicationContext;
import jp.spring.ioc.context.ApplicationContextAware;

/**
 * Created by Administrator on 1/3/2017.
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private final AbstractApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void postProcessBeforeInitialization() throws Exception {

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)  {
        if(bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
        }
        return bean;
    }
}

package jp.spring.ioc.beans.factory;

import jp.spring.ioc.beans.Autowireds;
import jp.spring.ioc.beans.BeanPostProcessor;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.impl.AutowireCapableBeanFactory;

/**
 * Created by Administrator on 1/9/2017.
 */
public class AutowireBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    AutowireCapableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {

        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws Exception {
        if(beanFactory instanceof AutowireCapableBeanFactory) {
            this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
        }
    }
}

package jp.spring.ioc.context;

import jp.spring.ioc.beans.BeanPostProcessor;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.impl.ApplicationContextAwareProcessor;

import java.util.List;


/**
 * Created by Administrator on 12/26/2016.
 */
public abstract  class AbstractApplicationContext  implements ApplicationContext{
    protected AbstractBeanFactory beanFactory;

    public AbstractApplicationContext(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void refresh() throws Exception {
        loadBeanDefinitions(beanFactory);
        registerBeanPostProcessors(beanFactory);
    }

    protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception;

    protected void registerBeanPostProcessors(AbstractBeanFactory beanFactory) throws Exception {
        List<BeanPostProcessor> beanPostProcessors = beanFactory.getBeansForType(BeanPostProcessor.class);
        for(Object beanPostProcessor : beanPostProcessors) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
        }

        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }

    protected void onRefresh() throws Exception {
    }

    @Override
    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }
}

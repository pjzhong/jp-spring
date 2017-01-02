package jp.spring.ioc.beans.factory;

import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.BeanPostProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 12/25/2016.
 * 简单的规定了如何获取和注册Bean
 * 至于具体如何创造Bean，则留给子类去实现
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    private final List<String> beanDefinitionIds = new ArrayList<String>();

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitionMap.get(name);
        if(beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + "is defined");
        }
        Object bean = beanDefinition.getBean();
        if(bean == null) {
            bean = doCreateBean(beanDefinition);
            bean = initializeBean(bean, name);
            beanDefinition.setBean(bean);
        }
        return bean;
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception{
        if(beanDefinitionMap.containsKey(name)) {
            throw new IllegalArgumentException("Bean " + name + "must be unique");
        }
        beanDefinitionMap.put(name, beanDefinition);
        beanDefinitionIds.add(name);
    }

    public void preInstantiateSingletons() throws Exception {
        for(Iterator<String> it = this.beanDefinitionIds.iterator(); it.hasNext();) {
            String beanName  = it.next();
            getBean(beanName);
        }
    }

    public <A> List<A> getBeansForType(Class<A> type) throws Exception {
        List<A> beans = new ArrayList<A>();
        for(String beanDefinitionName : beanDefinitionIds) {
            if(type.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getBeanClass())) {
                beans.add((A)getBean(beanDefinitionName));
            }
        }

        return beans;
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    protected Object initializeBean(Object bean, String name) throws Exception {
        if(!beanPostProcessors.isEmpty()) {
            for(BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessBeforeInitialization(bean, name);
            }

            for(BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
            }
        }

        return bean;
    }

    /**
     * initializing bean
     * */
    protected abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;
}

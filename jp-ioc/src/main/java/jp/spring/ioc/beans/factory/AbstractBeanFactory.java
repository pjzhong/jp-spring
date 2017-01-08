package jp.spring.ioc.beans.factory;

import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.BeanPostProcessor;
import jp.spring.ioc.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 12/25/2016.
 * 简单的规定了如何获取和注册Bean
 * 至于具体如何创造Bean，则留给子类去实现
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    private Map<String, BeanDefinition> beanNameDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    private Map<Class<?>, BeanDefinition> beanClassDefinitionMap = new ConcurrentHashMap<Class<?>, BeanDefinition>();

    private final List<String> beanDefinitionIds = new ArrayList<String>();

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanNameDefinitionMap.get(name);
        if(beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + " is defined");
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
    public Object getBean(Class<?> beanClass) throws Exception {
        BeanDefinition beanDefinition = beanClassDefinitionMap.get(beanClass);
        if(beanDefinition == null) {
            throw new IllegalArgumentException("No bean class " + beanClass + "is defined");
        }

        Object bean = beanDefinition.getBean();
        if(bean == null) {
            bean = doCreateBean(beanDefinition);
            bean = initializeBean(bean, null);
            beanDefinition.setBean(bean);
        }

        return bean;
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

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception{
        if(beanNameDefinitionMap.containsKey(name)) {
            throw new IllegalArgumentException("Bean " + name + "must be unique");
        }
        beanNameDefinitionMap.put(name, beanDefinition);
        beanClassDefinitionMap.put(beanDefinition.getBeanClass(), beanDefinition);
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
            if(type.isAssignableFrom(beanNameDefinitionMap.get(beanDefinitionName).getBeanClass())) {
                beans.add((A)getBean(beanDefinitionName));
            }
        }

        return beans;
    }

    public List<String> getBeanNamesForType(Class targetType) {
        List<String> result = new ArrayList<String>();

        boolean matchFound;
        for(String beanName : beanDefinitionIds) {
            BeanDefinition beanDefinition = beanNameDefinitionMap.get(beanName);
            matchFound = targetType.isAssignableFrom( beanDefinition.getBeanClass());
            if(matchFound) {
                result.add(beanName);
            }
        }

        return result;
    }




    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * initializing bean
     * */
    protected abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;
}

package jp.spring.ioc.beans.factory;

import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.util.JpUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 12/25/2016.
 * 简单的规定了如何获取和注册Bean,
 * 至于如何创造Bean，则留给子类去实现
 */
public abstract class AbstractBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanNameDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    private final Map<Class<?>, String[]> beanNamesByType = new ConcurrentHashMap<Class<?>, String[]>();

    private final Map<Class<?>, List<?>> beansByType = new HashMap<>();

    private final Map<Class<? extends Annotation>, String[]> beanNamesByAnnotation = new HashMap<>();

    private final List<String> beanDefinitionIds = new ArrayList<String>();

    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    private final Properties properties = new Properties();

    public void refresh() {
        try {
            beforeInitializeBean();
        } catch (Exception e) {
            throw new RuntimeException("Error Raised when refresh factory", e);
        }
    }

    /**
     * initializing bean
     * */
    protected abstract Object doCreateBean(BeanDefinition beanDefinition) throws Exception;

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception{
        if(beanNameDefinitionMap.containsKey(name)) {
            throw new IllegalArgumentException("Bean name " + name + " must be unique");
        }
        beanNameDefinitionMap.put(name, beanDefinition);
        beanDefinitionIds.add(name);
    }

    @Override
    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanNameDefinitionMap.get(name);
        if(beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + " is defined");
        }
        Object bean = beanDefinition.getBean();
        if(bean == null) {
            bean = doCreateBean(beanDefinition);
            bean = afterInitializeBean(bean, name);
            beanDefinition.setBean(bean);
        }
        return beanDefinition.getBean();
    }

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
        this.beanPostProcessors.add(beanPostProcessor);
    }

    protected void beforeInitializeBean() throws Exception {
        if(!beanPostProcessors.isEmpty()) {
            for(BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                beanPostProcessor.postProcessBeforeInitialization();
            }
        }
    }

    protected Object afterInitializeBean(Object bean, String name) throws Exception {
        if(!beanPostProcessors.isEmpty()) {
            for(BeanPostProcessor beanPostProcessor : beanPostProcessors) {
                bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
            }
        }

        invokeAware(bean);
        return bean;
    }

    private void invokeAware(Object bean) throws Exception{
        if(bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
    }

    //various getters
    public Class<?> getType (String name) {
        if(beanNameDefinitionMap.get(name) != null) {
            return beanNameDefinitionMap.get(name).getBeanClass();
        }
        return  null;
    }

    public <A> List<A> getBeansForType(Class<A> type) throws Exception {
        if(beansByType.get(type) != null) {
            return (List<A>)beansByType.get(type);
        }

        List<A> beans = new ArrayList<A>();
        for(String beanDefinitionName : beanDefinitionIds) {
            if(type.isAssignableFrom(beanNameDefinitionMap.get(beanDefinitionName).getBeanClass())) {
                beans.add((A) getBean(beanDefinitionName));
            }
        }
        if(!JpUtils.isEmpty(beans)) {
            beansByType.put(type, beans);
        }
        return beans;
    }

    public List<String> getBeanNamesForType(Class<?> targetType) {
        List<String> result = new ArrayList<String>();

        String[] temp = beanNamesByType.get(targetType);
        if(temp != null) {
            result = Arrays.asList(temp);
            return result;
        }

        boolean matchFound;
        for(String beanName : beanDefinitionIds) {
            BeanDefinition beanDefinition = beanNameDefinitionMap.get(beanName);
            matchFound = targetType.isAssignableFrom( beanDefinition.getBeanClass());
            if(matchFound) {
                result.add(beanName);
            }
        }

        if(!JpUtils.isEmpty(result)) {
            beanNamesByType.put(targetType, result.toArray(new String[result.size()]));
        }
        return result;
    }

    public List<String> getBeanNamByAnnotation(Class<? extends Annotation> annotation) {
        if(beanNamesByAnnotation.get(annotation) != null) {
            String[] names = beanNamesByAnnotation.get(annotation);
            return Arrays.asList(names);
        }

        List<String> result = new ArrayList<String>();
        BeanDefinition beanDefinition;
        for(String beanName : beanDefinitionIds) {
            beanDefinition = beanNameDefinitionMap.get(beanName);
            if(JpUtils.isAnnotated(beanDefinition.getBeanClass(), annotation)) {
               result.add(beanName);
            }
        }

        if(!JpUtils.isEmpty(result)) {
            beanNamesByAnnotation.put(annotation, result.toArray(new String[result.size()]));
        }
        return result;
    }

    public Properties getProperties() {
        return properties;
    }
}

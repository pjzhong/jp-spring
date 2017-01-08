package jp.spring.ioc.beans.factory.impl;

import jp.spring.ioc.aop.BeanFactoryAware;
import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.BeanReference;
import jp.spring.ioc.beans.PropertyValue;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 12/25/2016.
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    protected void resolveDependency(Object bean) throws Exception {
        Field[] fields = bean.getClass().getFields();

    }

    protected Object resolveDependency(Class<?> dependencyType, String beanName, Set<String> autowiredBeanNames) throws Exception {
        Map<String, Object> matchingBeans = findAutowireCandidates(beanName, dependencyType);
        Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();

        return entry.getValue();
    }


    @Override
    protected Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        beanDefinition.setBean(bean);
        resolveDependency(bean);
        injectPropertyValue(bean, beanDefinition);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition.getBeanClass().newInstance();
    }

    protected void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
        //只是为了方便易懂
        if(bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }

        for(PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
            Object value = propertyValue.getValue();
            if(value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getName());
            }

            try {
                Method declaredMethod = bean.getClass().getDeclaredMethod(
                        "set" + StringUtils.upperFirst(propertyValue.getName()), value.getClass());

                declaredMethod.setAccessible(true);
                declaredMethod.invoke(bean, value);
            } catch (NoSuchMethodException e) {
                Field declareField = bean.getClass().getDeclaredField(propertyValue.getName());
                declareField.setAccessible(true);
                declareField.set(bean, value);
            }
        }
    }

    protected Map<String, Object> findAutowireCandidates(String beanName, Class<?> requiredType) {
        Map<String, Object> result = null;

        try {
            List<String> candidateNames = getBeanNamesForType(requiredType);
            result = new LinkedHashMap<>(candidateNames.size());
            for (String candidateName : candidateNames) {
                if(!candidateName.equals(beanName)) {
                    result.put(candidateName, getBean(candidateName));
                }
            }
        } catch (Exception e) {
            result = null;
        }

        return result;
    }
}

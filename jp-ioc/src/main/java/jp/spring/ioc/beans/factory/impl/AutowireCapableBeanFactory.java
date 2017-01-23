package jp.spring.ioc.beans.factory.impl;

import jp.spring.ioc.BeansException;
import jp.spring.ioc.beans.*;
import jp.spring.ioc.beans.aware.BeanFactoryAware;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.support.*;
import jp.spring.ioc.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Administrator on 12/25/2016.
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        beanDefinition.setBean(bean);
        resolveDependency(bean, beanDefinition);
        injectPropertyValue(bean, beanDefinition);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition.getBeanClass().newInstance();
    }

    protected void resolveDependency(Object bean, BeanDefinition beanDefinition)  throws Exception{
        Autowireds autowireds = beanDefinition.getAutowireds();

        Object value;
        for(Autowired autowired : autowireds.getAutowiredList()) {
                Map<String, Object> matchingBeans = findAutowireCandidates(autowired.getId(), autowired.getAutowiredType());
                if(matchingBeans.isEmpty() && autowired.isRequired()) {
                    if(autowired.isRequired()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Inject ")
                                .append(autowired.getAutowiredType())
                                .append(" to ")
                                .append(beanDefinition.getBeanClass())
                                .append(" failed");
                        throw new BeansException(stringBuilder.toString());
                    }
                }

               //现在还没有写如何处理个多个符合类型的情况，所以只是简单的选择第一个
                value = matchingBeans.entrySet().iterator().next().getValue();
                autowired.inject(bean, value);
        }
    }

    protected void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
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
            result = new LinkedHashMap<String, Object>(candidateNames.size());
            for (String candidateName : candidateNames) {
                result.put(candidateName, getBean(candidateName));
            }
        } catch (Exception e) {
            result = null;
        }

        return result;
    }

    public static void main(String[] args) {
        Map<String, String> stringStringMap = new HashMap<>();

        System.out.println(stringStringMap.entrySet().iterator().next());
    }
}

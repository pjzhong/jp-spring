package jp.spring.ioc.beans.factory.impl;

import jp.spring.ioc.BeansException;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.support.*;
import jp.spring.ioc.util.JpUtils;
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
        List<InjectField> fields = beanDefinition.getInjectFields();
        if(JpUtils.isEmpty(fields)) {
            return;
        }

        Object value;
        for(InjectField injectField :beanDefinition.getInjectFields()) {
                Map<String, Object> matchingBeans = findAutowireCandidates(injectField.getId(), injectField.getAutowiredType());
                if(matchingBeans.isEmpty() && injectField.isRequired()) {
                    if(injectField.isRequired()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Inject ")
                                .append(injectField.getAutowiredType())
                                .append(" to ")
                                .append(beanDefinition.getBeanClass())
                                .append(" failed");
                        throw new BeansException(stringBuilder.toString());
                    }
                }

               //现在还没有写如何处理个多个符合类型的情况，所以只是简单的选择第一个
                value = matchingBeans.entrySet().iterator().next().getValue();
                injectField.inject(bean, value);
        }
    }

    protected void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
        List<PropertyValue> values = beanDefinition.getPropertyValues();
        if(values == null) {
            return;
        }

        for(PropertyValue propertyValue : beanDefinition.getPropertyValues()) {

            Object value = propertyValue.getValue();
            if(value == null) { // value的来源：用户在XML文件里声明，或者放在在properties文件里面
                try {
                    String strValue =  getProperties().getProperty(propertyValue.getName());
                    if(JpUtils.isPrimietive(propertyValue.getField().getType())) {
                        value = JpUtils.convert(strValue, propertyValue.getField().getType());
                    }
                } catch (Exception e) {
                    if(propertyValue.isRequired()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("inject ").append(propertyValue.getName())
                                .append(" to ").append(beanDefinition.getBeanClassName())
                                .append("failed");
                        throw new BeansException(builder.toString(), e);
                    }
                }

            }

            if(value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getName());
            }

            //空值没有注入的必要
            if(value == null) {
                continue;
            }

            try {
                Method declaredMethod = bean.getClass().getDeclaredMethod(
                        "set" + StringUtils.upperFirst(propertyValue.getName()), value.getClass());

                declaredMethod.setAccessible(true);
                declaredMethod.invoke(bean, value);
            } catch (NoSuchMethodException e) {
                Field declareField = propertyValue.getField();
                if(declareField == null) {
                    declareField = bean.getClass().getDeclaredField(propertyValue.getName());
                }
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
}

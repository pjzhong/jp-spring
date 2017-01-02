package jp.spring.ioc.beans.factory;

import jp.spring.ioc.aop.BeanFactoryAware;
import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.BeanReference;
import jp.spring.ioc.beans.PropertyValue;
import jp.spring.ioc.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 12/25/2016.
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        beanDefinition.setBean(bean);
        injectPropertyValue(bean, beanDefinition);
        return bean;
    }

    protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        return beanDefinition.getBeanClass().newInstance();
    }

    protected void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
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
}

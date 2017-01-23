package jp.spring.ioc.beans.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 12/24/2016.
 */
public class BeanDefinition {

    private Object bean;

    private Class<?> beanClass;

    private String beanClassName;

    private List<PropertyValue> propertyValues = null;

    private List<InjectField> injectFields = null;

    public BeanDefinition() {
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        try {
            this.beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object getBean() {
        return bean;
    }

    public void add(InjectField injectField) {
        if(injectFields == null) {
            injectFields = new ArrayList<>();
        }
        injectFields.add(injectField);
    }

    public void add(PropertyValue propertyValue) {
        if(propertyValues == null) {
            propertyValues = new ArrayList<>();
        }

        propertyValues.add(propertyValue);
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public List<InjectField> getInjectFields() {
        return injectFields;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClassName='" + beanClassName + '\'' +
                ", propertyValues=" + propertyValues +
                ", injectFields=" + injectFields +
                '}';
    }
}

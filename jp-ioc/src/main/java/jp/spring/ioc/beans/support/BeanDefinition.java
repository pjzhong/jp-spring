package jp.spring.ioc.beans.support;

/**
 * Created by Administrator on 12/24/2016.
 */
public class BeanDefinition {

    private Object bean;

    private Class<?> beanClass;

    private String beanClassName;

    private PropertyValues propertyValues = new PropertyValues();

    private Autowireds autowireds = new Autowireds();

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

    public PropertyValues getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void setAutowireds(Autowireds autowireds) {
        this.autowireds = autowireds;
    }

    public Autowireds getAutowireds() {
        return autowireds;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "beanClassName='" + beanClassName + '\'' +
                ", propertyValues=" + propertyValues +
                ", autowireds=" + autowireds +
                '}';
    }
}

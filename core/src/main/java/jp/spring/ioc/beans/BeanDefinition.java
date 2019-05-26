package jp.spring.ioc.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 12/24/2016.
 */
public class BeanDefinition {

  private Object bean;

  private Class<?> clazz;

  private List<PropertyValue> propertyValues = null;

  private List<InjectField> injectFields = null;

  public BeanDefinition() {
  }

  public BeanDefinition(Class<?> clazz, Object bean) {
    this.clazz = clazz;
    this.bean = bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public String getBeanClassName() {
    return clazz.getName();
  }


  public Object getBean() {
    return bean;
  }

  public void add(InjectField injectField) {
    if (injectFields == null) {
      injectFields = new ArrayList<>();
    }
    injectFields.add(injectField);
  }

  public void add(PropertyValue propertyValue) {
    if (propertyValues == null) {
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
        "beanClassName='" + clazz.getName() + '\'' +
        ", propertyValues=" + propertyValues +
        ", injectFields=" + injectFields +
        '}';
  }
}

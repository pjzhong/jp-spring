package jp.spring.ioc.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Bean数据类
 *
 * @author ZJP
 * @since 2019年05月28日 20:38:49
 **/
public class BeanDefinition {

  /** 实体对象 */
  private Object bean;
  /** 实力类 */
  private Class<?> clazz;
  /** 配置字段 */
  private List<PropertyValue> propertyValues;
  /** 装配字段 */
  private List<InjectField> injectFields;

  public BeanDefinition(Class<?> clazz) {
    this.clazz = clazz;
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
    return ObjectUtils.defaultIfNull(propertyValues, Collections.emptyList());
  }

  public List<InjectField> getInjectFields() {
    return ObjectUtils.defaultIfNull(injectFields, Collections.emptyList());
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

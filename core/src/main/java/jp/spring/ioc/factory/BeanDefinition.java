package jp.spring.ioc.factory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bean数据类
 *
 * @author ZJP
 * @since 2019年05月28日 20:38:49
 **/
public class BeanDefinition {

  /** 名字 */
  private String name;
  /** 实体类 */
  private Class<?> clazz;
  /** 注入后需要调用的方法 */
  private Method post;
  /** 配置字段 */
  private List<PropertyValue> propertyValues;
  /** 装配字段 */
  private List<InjectField> injectFields;

  public BeanDefinition(String name, Class<?> clazz) {
    this.name = name;
    this.clazz = clazz;
    this.propertyValues = Collections.emptyList();
    this.injectFields = Collections.emptyList();
  }

  public BeanDefinition(String name, Class<?> clazz,
      List<PropertyValue> propertyValues,
      List<InjectField> injectFields, Method post) {
    this.name = name;
    this.clazz = clazz;
    this.propertyValues = propertyValues;
    this.injectFields = injectFields;
    this.post = post;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public String getClassName() {
    return clazz.getName();
  }

  public String getName() {
    return name;
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

  public Method getPost() {
    return post;
  }

  public List<PropertyValue> getPropertyValues() {
    return propertyValues;
  }

  public List<InjectField> getInjectFields() {
    return injectFields;
  }

  @Override
  public String toString() {
    return "BeanDefinition{" + "name='" + name + '\''
        + '}';
  }
}

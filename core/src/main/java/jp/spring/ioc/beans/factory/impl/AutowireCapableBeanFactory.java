package jp.spring.ioc.beans.factory.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.beans.support.BeanReference;
import jp.spring.ioc.beans.support.InjectField;
import jp.spring.ioc.beans.support.PropertyValue;
import jp.spring.ioc.util.StringUtils;
import jp.spring.ioc.util.TypeUtil;

/**
 * Created by Administrator on 12/25/2016.
 */
public class AutowireCapableBeanFactory extends AbstractBeanFactory {

  @Override
  protected Object doCreateBean(BeanDefinition beanDefinition) {
    Object bean = null;
    try {
      bean = createBeanInstance(beanDefinition);
      beanDefinition.setBean(bean);
      resolveDependency(bean, beanDefinition);
      injectPropertyValue(bean, beanDefinition);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bean;
  }

  protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
    return beanDefinition.getBeanClass().newInstance();
  }

  protected void resolveDependency(Object bean, BeanDefinition beanDefinition) throws Exception {
    List<InjectField> fields = beanDefinition.getInjectFields();
    if (TypeUtil.isEmpty(fields)) {
      return;
    }

    /* 两种情况:
     * 1.没有@Qualifier, 那么根据类型来获取注入对象。多个取第一个
     * 2.用户添加了@Qualifier, 使用@Qualifier的值来获取注入对象
     */
    Object value = null;
    for (InjectField injectField : beanDefinition.getInjectFields()) {

      if (StringUtils.isEmpty(injectField.getId())) {
        Map<String, Object> matchingBeans = findAutowireCandidates(injectField.getId(),
            injectField.getAutowiredType());
        if (!TypeUtil.isEmpty(matchingBeans)) {
          value = matchingBeans.entrySet().iterator().next().getValue();
        }
      } else {
        value = getBean(injectField.getId());
      }

      if (value == null && injectField.isRequired()) {
        if (injectField.isRequired()) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Inject ")
              .append(injectField.getAutowiredType())
              .append(" to ")
              .append(beanDefinition.getBeanClass())
              .append(" failed");
          throw new BeansException(stringBuilder.toString());
        }
      }

      injectField.inject(bean, value);
    }
  }

  protected void injectPropertyValue(Object bean, BeanDefinition beanDefinition) throws Exception {
    List<PropertyValue> values = beanDefinition.getPropertyValues();
    if (values == null) {
      return;
    }

    for (PropertyValue propertyValue : beanDefinition.getPropertyValues()) {
      Object value = propertyValue.getValue();
      if (value == null) { // value的来源：用户在XML文件里声明，或者放在在properties文件里面
        String strValue = getProperties().getProperty(propertyValue.getName());
        if ((strValue != null) && TypeUtil.isSimpleType(propertyValue.getField().getType())) {
          value = TypeUtil.convert(strValue, propertyValue.getField().getType());
        }
      }

      if (value instanceof BeanReference) {
        BeanReference beanReference = (BeanReference) value;
        value = getBean(beanReference.getName());
      }

      if (value == null && propertyValue.isRequired()) {
        StringBuilder builder = new StringBuilder();
        builder.append("Inject ").append(propertyValue.getName())
            .append(" to ").append(beanDefinition.getBeanClassName())
            .append(" failed");
        throw new BeansException(builder.toString());
      } else if (value == null) { //空值没有注入的必要
        continue;
      }

      try {
        Method declaredMethod = bean.getClass().getDeclaredMethod(
            "set" + StringUtils.upperFirst(propertyValue.getField().getName()), value.getClass());

        declaredMethod.setAccessible(true);
        declaredMethod.invoke(bean, value);
      } catch (NoSuchMethodException e) {
        Field declareField = propertyValue.getField();
        if (declareField == null) {
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

package jp.spring.ioc.beans.support;

import java.lang.reflect.Field;
import java.util.Objects;
import jp.spring.ioc.beans.factory.annotation.Value;
import org.apache.commons.lang3.StringUtils;

/**
 * 配置信息注入
 *
 * @author ZJP
 * @since 2019年05月24日 16:36:29
 **/
public class PropertyValue {

  private String name;
  private Field field;
  private boolean isRequired = false;

  public PropertyValue() {
  }

  public PropertyValue(Field field, Value value) {
    field.setAccessible(true);
    String name = value.value();
    if (StringUtils.isBlank(name)) {
      name = field.getName();
    }
    this.name = StringUtils.uncapitalize(name);
    this.field = field;
    this.isRequired = value.required();
  }

  public String getName() {
    return name;
  }

  public boolean isRequired() {
    return isRequired;
  }

  public Field getField() {
    return field;
  }

  public void inject(Object bean, Object value) throws IllegalAccessException {
    field.set(bean, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyValue that = (PropertyValue) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}

package jp.spring.ioc.beans;

import java.lang.reflect.Field;
import java.util.Objects;
import jp.spring.ioc.beans.factory.annotation.Autowired;

/**
 * 自动装配字段
 *
 * @author ZJP
 * @since 2019年05月24日 16:17:58
 **/
public class InjectField {

  /** 是否必须 */
  private final boolean isRequired;
  /** 字段对象 */
  private final Field field;
  /** 候选者 */
  private final String qualifier;

  public InjectField(String qualifier, Field field, Autowired auto) {
    this.qualifier = qualifier;
    this.field = field;
    this.field.setAccessible(true);
    this.isRequired = auto.required();
  }

  public boolean isRequired() {
    return isRequired;
  }

  public String getQualifier() {
    return qualifier;
  }

  public void inject(Object target, Object value) throws Exception {
    field.set(target, value);
  }

  public Class<?> getType() {
    return field.getType();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InjectField that = (InjectField) o;
    return field.equals(that.field) &&
        qualifier.equals(that.qualifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, qualifier);
  }

  @Override
  public String toString() {
    return "InjectField{" + "field=" + field
        + ", qualifier='" + qualifier + '\''
        + '}';
  }
}

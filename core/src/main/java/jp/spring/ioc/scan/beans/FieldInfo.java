package jp.spring.ioc.scan.beans;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jp.spring.ioc.scan.utils.ReflectionUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 字段信息
 *
 * @author ZJP
 * @since 2019年05月31日 17:16:14
 **/
public class FieldInfo {

  private final String belongToClass;
  private final String fieldName;
  private final int modifiers;
  private final String typeStr;
  private Object constantValue = null;
  private Map<String, AnnotationInfo> annotations = Collections.emptyMap();

  FieldInfo(String className, String fieldName, String typeDescriptor, int modifiers) {
    this.belongToClass = className;
    this.fieldName = fieldName;
    this.modifiers = modifiers;

    final List<String> typeNames = ReflectionUtils.parseTypeDescriptor(typeDescriptor);
    if (typeNames.size() != 1) {
      throw new IllegalArgumentException("Invalid type descriptor for field: " + typeDescriptor);
    }
    this.typeStr = typeNames.get(0);
  }


  void setAnnotations(Map<String, AnnotationInfo> annotations) {
    this.annotations = Collections.unmodifiableMap(annotations);
  }

  void setConstantValue(Object constantValue) {
    this.constantValue = constantValue;
  }

  /**
   * Get the field modifiers as a string, e.g. "public static final".
   */
  public String getModifiers() {
    return ReflectionUtils.modifiersToString(modifiers, false);
  }

  /**
   * Returns true if this field is public.
   */
  public boolean isPublic() {
    return Modifier.isPublic(modifiers);
  }

  /**
   * Returns true if this field is private.
   */
  public boolean isPrivate() {
    return Modifier.isPrivate(modifiers);
  }

  /**
   * Returns true if this field is protected.
   */
  public boolean isProtected() {
    return Modifier.isProtected(modifiers);
  }

  /**
   * Returns true if this field is package-private.
   */
  public boolean isPackagePrivate() {
    return !isPublic() && !isPrivate() && !isProtected();
  }

  /**
   * Returns true if this field is static.
   */
  public boolean isStatic() {
    return Modifier.isStatic(modifiers);
  }

  /**
   * Returns true if this field is final.
   */
  public boolean isFinal() {
    return Modifier.isFinal(modifiers);
  }

  /**
   * Returns true if this field is a transient field.
   */
  public boolean isTransient() {
    return Modifier.isTransient(modifiers);
  }

  /**
   * Returns the name of the field.
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Returns the access flags of the field.
   */
  public int getAccessFlags() {
    return modifiers;
  }

  /**
   * Returns the type of the field, in string representation (e.g. "int[][]").
   */
  public String getTypeStr() {
    return typeStr;
  }

  /**
   * maybe be null, if this field not modified by static and final or it isn't primitive type
   */
  public Object getConstantValue() {
    return constantValue;
  }

  public Map<String, AnnotationInfo> getAnnotations() {
    return ObjectUtils.defaultIfNull(annotations, Collections.emptyMap());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FieldInfo fieldInfo = (FieldInfo) o;
    return Objects.equals(belongToClass, fieldInfo.belongToClass) &&
        Objects.equals(fieldName, fieldInfo.fieldName) &&
        Objects.equals(typeStr, fieldInfo.typeStr);
  }

  @Override
  public int hashCode() {
    return Objects.hash(belongToClass, fieldName, typeStr);
  }

  @Override
  public String toString() {
    final StringBuilder buf = new StringBuilder();

    getAnnotations().values().forEach(a -> buf.append(a).append(' '));
    buf.append(getModifiers()).append(' ').append(getTypeStr()).append(' ').append(fieldName);

    if (getConstantValue() != null) {
      buf.append(" = ").append(constantValue).append(';');
    }

    return buf.toString();
  }

  public static FieldInfoBuilder builder(String className, String fieldName, String typeDescriptor,
      int modifiers) {
    return new FieldInfoBuilder(className, fieldName, typeDescriptor, modifiers);
  }
}

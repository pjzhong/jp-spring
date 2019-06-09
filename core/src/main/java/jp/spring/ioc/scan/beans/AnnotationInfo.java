package jp.spring.ioc.scan.beans;

import java.util.Iterator;
import java.util.Map;

public class AnnotationInfo {

  private final String name;
  private Map<String, ParamValue> params;

  public AnnotationInfo(String name, Map<String, ParamValue> params) {
    this.name = name;
    this.params = params;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(name);
    sb.append("(");
    Iterator<ParamValue> iterator = params.values().iterator();
    while (iterator.hasNext()) {
      sb.append(iterator.next());
      if (iterator.hasNext()) {
        sb.append(",  ");
      }
    }
    sb.append(")");

    return sb.toString();
  }

  public static class ParamValue {

    private final String name;
    private final Object value;

    public ParamValue(String paramName, Object value) {
      this.name = paramName;
      this.value = value;
    }

    @Override
    public String toString() {
      return name + "=" + value;
    }
  }

  //public static class classRef {
//
  //  private final String typeDescriptor;
//
  //  public classRef(String typeDescriptor) {
  //    this.typeDescriptor = typeDescriptor;
  //  }
//
  //  @Override
  //  public String toString() {
  //    return typeDescriptor;
  //  }
  //}

  //public static class enumRef {

  //  private final String name;
  //  private final String field;

  //  public enumRef(String name, String field) {
  //    this.field = field;
  //    this.name = name;
  //  }

  //  @Override
  //  public String toString() {
  //    return name + "." + field;
  //  }
  //}
}

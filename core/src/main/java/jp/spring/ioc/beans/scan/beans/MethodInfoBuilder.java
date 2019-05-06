package jp.spring.ioc.beans.scan.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 12/3/2017.
 */
public class MethodInfoBuilder {

  public MethodInfo build() {
    MethodInfo methodInfo = new MethodInfo(className, methodName, accessFlags, typeDescriptor);
    methodInfo.setAnnotations(annotationNames);
    methodInfo.setDefaultValues(defaultValue);
    methodInfo.setIsConstructor(isConstructor);
    return methodInfo;
  }

  MethodInfoBuilder(String className, String methodName, String typeDescriptor, int accessFlags) {
    this.className = className;
    this.isConstructor = "<init>".equals(methodName);
    this.methodName = this.isConstructor ? className : methodName;
    this.typeDescriptor = typeDescriptor;
    this.accessFlags = accessFlags;
  }

  public void addAnnotationName(AnnotationInfo annotation) {
    if (annotationNames.isEmpty()) {
      annotationNames = new HashMap<>();
    }
    annotationNames.put(annotation.getName(), annotation);
  }

  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  private final String className;
  private final String methodName;
  private final String typeDescriptor;
  private final int accessFlags;
  private final boolean isConstructor;
  private Map<String, AnnotationInfo> annotationNames = Collections.emptyMap();
  private Object defaultValue;//only annotation methods, may contains one or more default values
}

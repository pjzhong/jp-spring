package jp.spring.ioc.scan.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * 类信息构造者
 *
 * @author ZJP
 * @since 2019年05月26日 21:06:51
 **/
public class ClassInfoBuilder {

  private final String className;

  private final int accessFlag;
  private String superclassName;    // Superclass (can be null if no superclass, or if superclass is blacklisted)
  private Set<String> methodAnnotations = Collections.emptySet();
  private Set<String> fieldAnnotations = Collections.emptySet();
  private List<String> implementedInterfaces = Collections.emptyList();
  private Map<String, AnnotationInfo> annotations = Collections.emptyMap();
  private List<FieldInfo> fieldInfoList = Collections.emptyList();
  private List<MethodInfo> methodInfoList = Collections.emptyList();

  private Map<String, ClassInfo> infoMap; //intense share by all ClassInfoBuilder instance

  ClassInfoBuilder(final String className, final int accessFlag) {
    this.className = className;
    this.accessFlag = accessFlag;
  }

  ClassInfo addScannedClass(final String className) {
    ClassInfo classInfo;
    if (infoMap.containsKey(className)) {
      classInfo = infoMap.get(className);
    } else {
      infoMap.put(className, classInfo = new ClassInfo(className));
    }

    classInfo.scanned = true;
    classInfo.isInterface |= isInterface();
    classInfo.annotation |= isAnnotation();
    return classInfo;
  }

  ClassInfo getClassInfo(String className) {
    ClassInfo classInfo = infoMap.get(className);
    if (classInfo == null) {
      infoMap.put(className, classInfo = new ClassInfo(className));
    }
    return classInfo;
  }

  /**
   * Not thread safe
   *
   * @param infoMap for cache all classInfo Instances
   */
  void build(Map<String, ClassInfo> infoMap) {
    this.infoMap = infoMap;
    final ClassInfo classInfo = addScannedClass(className);

    if (StringUtils.isNotBlank(superclassName)) {
      classInfo.addSuperclass(superclassName, this);
    }

    implementedInterfaces.forEach(s -> classInfo.addImplementedInterface(s, this));
    annotations.values().forEach(a -> classInfo.addAnnotation(a, this));
    methodAnnotations.forEach(s -> classInfo.addMethodAnnotation(s, this));
    fieldAnnotations.forEach(s -> classInfo.addFieldAnnotation(s, this));
    classInfo.addFieldInfo(fieldInfoList);
    classInfo.addMethodInfo(methodInfoList);
  }

  public void addSuperclass(final String superclassName) {
    this.superclassName = superclassName;
  }

  public void addImplementedInterface(final String interfaceName) {
    if (implementedInterfaces.isEmpty()) {
      implementedInterfaces = new ArrayList<>();
    }
    implementedInterfaces.add(interfaceName);
  }

  public void addAnnotation(AnnotationInfo annotation) {
    if (annotations.isEmpty()) {
      annotations = new HashMap<>();
    }
    annotations.put(annotation.getName(), annotation);
  }

  public void addMethodAnnotation(AnnotationInfo annotation) {
    if (methodAnnotations.isEmpty()) {
      methodAnnotations = new HashSet<>();
    }
    methodAnnotations.add(annotation.getName());
  }

  public void addFieldAnnotation(AnnotationInfo annotation) {
    if (fieldAnnotations.isEmpty()) {
      fieldAnnotations = new HashSet<>();
    }
    fieldAnnotations.add(annotation.getName());
  }

  public void addFieldInfo(final FieldInfo fieldInfo) {
    if (fieldInfoList.isEmpty()) {
      fieldInfoList = new ArrayList<>();
    }
    fieldInfoList.add(fieldInfo);
  }

  public void addMethodInfo(final MethodInfo methodInfo) {
    if (methodInfoList.isEmpty()) {
      methodInfoList = new ArrayList<>();
    }
    methodInfoList.add(methodInfo);
  }

  public String getClassName() {
    return className;
  }

  public boolean isInterface() {
    return !isAnnotation() && ((accessFlag & 0x0200) != 0);
  }

  public boolean isAnnotation() {
    return (accessFlag & 0x2000) != 0;
  }

  public String getSuperclassName() {
    return superclassName;
  }

}

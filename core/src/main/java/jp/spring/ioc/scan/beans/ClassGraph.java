package jp.spring.ioc.scan.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Administrator on 11/12/2017.
 */
public class ClassGraph {

  private final Map<String, ClassInfo> classBeans;

  public static ClassGraphBuilder builder(Collection<ClassInfoBuilder> builders) {
    return new ClassGraphBuilder(builders);
  }

  ClassGraph(Map<String, ClassInfo> infoMap) {
    this.classBeans = infoMap;
  }

  public Set<ClassInfo> getAllInterfaces() {
    return classBeans.values()
        .parallelStream()
        .filter(ClassInfo::isInterface)
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getAllAnnotations() {
    return classBeans.values()
        .parallelStream()
        .filter(ClassInfo::isAnnotation)
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getAllStandardClass() {
    return classBeans.values()
        .parallelStream()
        .filter(ClassInfo::isStandardClass)
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getInfoOfClassSuperClassOf(Class<?> target) {
    final ClassInfo info = classBeans.get(target.getName());
    return ObjectUtils.defaultIfNull(info.getSuperClasses(), Collections.emptySet());
  }

  public Set<ClassInfo> getInfoOfClassSubClassOf(Class<?> target) {
    final ClassInfo info = classBeans.get(target.getName());
    return Optional.ofNullable(info)
        .map(ClassInfo::getSubClasses)
        .orElseGet(Collections::emptySet);
  }

  /**
   * return all standard class(include abstract) that implementing the specific interface,exclude
   * interface and annotation
   */
  public Set<ClassInfo> getInfoOfClassImplementing(Class<?> targetInterfaces) {
    final ClassInfo info = classBeans.get(targetInterfaces.getName());
    if (info == null) {
      return Collections.emptySet();
    }
    return info.getClassesImplementing()
        .stream()
        .filter(ClassInfo::isStandardClass)
        .collect(Collectors.toSet());
  }

  /**
   * return All class that have method(s) annotated by this specific annotation , exclude
   * annotation
   */
  public Set<ClassInfo> getInfoWithMethodAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(targetAnnotation.getName());
    if (info == null) {
      return Collections.emptySet();
    }

    return info.getClassesWithAnnotation()
        .stream()
        .filter(c -> c.isStandardClass() || c.isInterface())
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getInfoWithFieldAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(targetAnnotation.getName());

    return Optional.ofNullable(info)
        .map(ClassInfo::getClassesWithFieldAnnotation)
        .orElseGet(Collections::emptySet);
  }

  public Set<ClassInfo> getInfoWithAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(targetAnnotation.getName());

    return Optional.ofNullable(info)
        .map(ClassInfo::getClassesWithAnnotation)
        .orElseGet(Collections::emptySet);
  }
}

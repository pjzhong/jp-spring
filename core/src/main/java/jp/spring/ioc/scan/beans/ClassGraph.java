package jp.spring.ioc.scan.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jp.spring.ioc.scan.utils.ClassScanUtils;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Administrator on 11/12/2017.
 */
public class ClassGraph {

  public Set<ClassInfo> getAllInterfaces() {
    return classBeans.values()
        .parallelStream()
        .filter(c -> c.isInterface() && c.isClassFileScanned())
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getAllAnnotations() {
    return classBeans.values()
        .parallelStream()
        .filter(c -> c.isAnnotation() && c.isClassFileScanned())
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getAllStandardClass() {
    return classBeans.values()
        .parallelStream()
        .filter(c -> c.isStandardClass() && c.isClassFileScanned())
        .collect(Collectors.toSet());
  }

  public Set<ClassInfo> getInfoOfClassSuperClassOf(Class<?> target) {
    final ClassInfo info = classBeans.get(target.getName());
    return ObjectUtils.defaultIfNull(info.getSuperClasses(), Collections.emptySet());
  }

  public Set<ClassInfo> getInfoOfClassSubClassOf(Class<?> target) {
    final ClassInfo info = classBeans.get(target.getName());
    return ObjectUtils.defaultIfNull(info.getSubClasses(), Collections.emptySet());
  }

  /**
   * return all standard class(include abstract) that implementing the specific interface,exclude
   * interface and annotation
   */
  public Set<ClassInfo> getInfoOfClassImplementing(Class<?> targetInterfaces) {
    final ClassInfo info = classBeans.get(ClassScanUtils.interfaceName(targetInterfaces));
    if (info == null) {
      return Collections.emptySet();
    } else {
      return filterClassInfo(info.getClassesImplementing(),
          ClassType.STANDARD_CLASS);
    }
  }

  /***
   * return All class that annotated by the specific annotation , exclude annotation
   */
  public Set<ClassInfo> getInfoOfClassesWithMethodAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(ClassScanUtils.annotationName(targetAnnotation));
    if (info == null) {
      return Collections.emptySet();
    } else {
      return filterClassInfo(info.getClassesWithMethodAnnotation(),
          ClassType.STANDARD_CLASS, ClassType.INTERFACES);
    }
  }

  public Set<ClassInfo> getInfoOfClassesWithFieldAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(ClassScanUtils.annotationName(targetAnnotation));
    return (info == null) ? Collections.emptySet()
        : info.getClassesWithFieldAnnotation();
  }

  public Set<ClassInfo> getInfoOfClassesWithAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(ClassScanUtils.annotationName(targetAnnotation));
    return (info == null) ? Collections.emptySet()
        : info.getClassesWithAnnotation();
  }

  private Set<ClassInfo> filterClassInfo(Set<ClassInfo> infoSet, ClassType... classTypes) {
    if (infoSet == null) {
      return Collections.emptySet();
    }

    boolean includeAllTypes = (classTypes.length == 0);
    boolean includeStandardClasses = false;
    boolean includeInterfaces = false;
    boolean includeAnnotations = false;

    for (ClassType classType : classTypes) {
      if (includeAllTypes) {
        break;
      }
      switch (classType) {
        case ALL:
          includeAllTypes = true;
          break;
        case STANDARD_CLASS:
          includeStandardClasses = true;
          break;
        case INTERFACES:
          includeInterfaces = true;
          break;
        case ANNOTATION:
          includeAnnotations = true;
          break;
        case INTERFACE_OR_ANNOTATION:
          includeInterfaces = includeAnnotations = true;
          break;
      }
    }

    if ((includeStandardClasses && includeAnnotations && includeInterfaces) || includeAllTypes) {
      return infoSet;
    }

    Set<ClassInfo> infoAfterFiltered = new HashSet<>(infoSet.size());
    for (ClassInfo info : infoSet) {
      if ((includeStandardClasses && info.isStandardClass())
          || (includeInterfaces && info.isInterface())
          || (includeAnnotations && info.isAnnotation())
      ) {
        if (info.isClassFileScanned()) {
          infoAfterFiltered.add(info);
        }
      }
    }

    return infoAfterFiltered;
  }

  public static ClassGraphBuilder builder(
      Collection<ClassInfoBuilder> builders) {
    return new ClassGraphBuilder(builders);
  }

  ClassGraph(Map<String, ClassInfo> infoMap) {
    this.classBeans = infoMap;
  }

  private final Map<String, ClassInfo> classBeans;

  private enum ClassType {
    ALL,
    STANDARD_CLASS,
    INTERFACES,
    ANNOTATION,
    INTERFACE_OR_ANNOTATION
  }
}

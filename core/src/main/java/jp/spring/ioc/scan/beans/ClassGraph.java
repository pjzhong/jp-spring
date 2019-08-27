package jp.spring.ioc.scan.beans;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * 类关系图
 *
 * @author ZJP
 * @since 2019年05月31日 17:16:43
 **/
public class ClassGraph {

  private Map<String, ClassInfo> clazzs;

  public static ClassGraph build() {
    return build(Collections.emptySet());
  }

  public static ClassGraph build(Iterable<ClassData> builders) {
    return new ClassGraph(builders);
  }

  private ClassGraph(Iterable<ClassData> builders) {
    clazzs = new ConcurrentHashMap<>();
    builders.forEach(this::addScannedClass);
  }

  private void addScannedClass(ClassData builder) {
    ClassInfo classInfo = clazzs.computeIfAbsent(builder.getName(), ClassInfo::new);
    if (StringUtils.isNotBlank(builder.getSuperclassName())) {
      classInfo.addSuperclass(classInfo(builder.getSuperclassName()));
    }

    builder.getImplemented().forEach(i -> classInfo.addImplemented(classInfo(i)));
    builder.getAnnotations().forEach(a -> classInfo.addAnnotation(classInfo(a)));

    classInfo.scanned = true;
    classInfo.accessFlag = builder.getAccessFlag();
  }

  private ClassInfo classInfo(String className) {
    return clazzs.computeIfAbsent(className, ClassInfo::new);
  }

  public Optional<ClassInfo> getInfo(String name) {
    return Optional.ofNullable(clazzs.get(name));
  }

  public Set<ClassInfo> getInfoWithAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = clazzs.get(targetAnnotation.getName());

    return Optional.ofNullable(info)
        .map(ClassInfo::getClassesWithAnnotation)
        .orElseGet(Collections::emptySet);
  }
}

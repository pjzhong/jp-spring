package jp.spring.ioc.scan.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 类关系图
 *
 * @author ZJP
 * @since 2019年05月31日 17:16:43
 **/
public class ClassGraph {

  private final Map<String, ClassInfo> classBeans;

  public static ClassGraphBuilder builder(Collection<ClassInfoBuilder> builders) {
    return new ClassGraphBuilder(builders);
  }

  ClassGraph(Map<String, ClassInfo> infoMap) {
    this.classBeans = infoMap;
  }

  public Set<ClassInfo> getInfoWithAnnotation(Class<?> targetAnnotation) {
    final ClassInfo info = classBeans.get(targetAnnotation.getName());

    return Optional.ofNullable(info)
        .map(ClassInfo::getClassesWithAnnotation)
        .orElseGet(Collections::emptySet);
  }
}

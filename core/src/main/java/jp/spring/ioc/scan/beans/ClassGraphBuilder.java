package jp.spring.ioc.scan.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/22.
 */
public class ClassGraphBuilder {

  public ClassGraph build() {
    Map<String, ClassInfo> infoMap = new HashMap<>(builders.size());
    for (ClassInfoBuilder builder : builders) {
      builder.build(infoMap);
    }

    return new ClassGraph(infoMap);
  }

  ClassGraphBuilder(Collection<ClassInfoBuilder> builders) {
    this.builders = builders;
  }

  private final Collection<ClassInfoBuilder> builders;
}

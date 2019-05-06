package jp.spring.ioc.beans.scan.beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jp.spring.ioc.beans.scan.scanner.ScanSpecification;

/**
 * Created by Administrator on 2017/11/22.
 */
public class ClassGraphBuilder {

  public ClassGraph build() {
    Map<String, ClassInfo> infoMap = new HashMap<>(builders.size( ));
    for (ClassInfoBuilder builder : builders) {
      builder.build(infoMap);
    }

    return new ClassGraph(specification, infoMap);
  }

  ClassGraphBuilder(ScanSpecification specification, Collection<ClassInfoBuilder> builders) {
    this.specification = specification;
    this.builders = builders;
  }

  private final ScanSpecification specification;
  private final Collection<ClassInfoBuilder> builders;
}

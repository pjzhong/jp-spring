package jp.spring.ioc.scan.scan;

import java.util.List;
import java.util.Properties;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;

/**
 * 类路径读取结果
 *
 * @author ZJP
 * @since 2019年05月25日 21:15:40
 **/
class ReadResult {

  /** 类信息构造器 */
  private List<ClassInfoBuilder> builders;
  /** 配置文件信息 */
  private Properties properties;

  List<ClassInfoBuilder> getBuilders() {
    return builders;
  }

  ReadResult setBuilders(List<ClassInfoBuilder> builders) {
    this.builders = builders;
    return this;
  }

  Properties getProperties() {
    return properties;
  }

  ReadResult setProperties(Properties properties) {
    this.properties = properties;
    return this;
  }
}

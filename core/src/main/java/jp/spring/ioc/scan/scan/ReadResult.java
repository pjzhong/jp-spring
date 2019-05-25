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
public class ReadResult {

  /** 类信息构造器 */
  public List<ClassInfoBuilder> builders;
  /** 配置文件信息 */
  private Properties properties;

  public List<ClassInfoBuilder> getBuilders() {
    return builders;
  }

  public ReadResult setBuilders(List<ClassInfoBuilder> builders) {
    this.builders = builders;
    return this;
  }

  public Properties getProperties() {
    return properties;
  }

  public ReadResult setProperties(Properties properties) {
    this.properties = properties;
    return this;
  }
}

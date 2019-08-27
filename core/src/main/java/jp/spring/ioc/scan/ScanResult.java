package jp.spring.ioc.scan;

import java.util.Properties;
import jp.spring.ioc.scan.beans.ClassGraph;

/**
 * 类扫描结果
 *
 * @author ZJP
 * @since 2019年05月25日 22:20:11
 **/
public class ScanResult {

  /** 类图 */
  private ClassGraph classGraph;

  /** 配置信息 */
  private Properties properties;

  private ScanResult() {
  }

  public static ScanResult of(ClassGraph graph, Properties properties) {
    ScanResult result = new ScanResult();
    result.classGraph = graph;
    result.properties = properties;
    return result;
  }

  public ClassGraph getClassGraph() {
    return classGraph;
  }

  public Properties getProperties() {
    return properties;
  }
}


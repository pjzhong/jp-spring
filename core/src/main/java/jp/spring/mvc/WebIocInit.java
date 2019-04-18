package jp.spring.mvc;

import jp.spring.mvc.context.WebApplicationContext;

/**
 * Created by Administrator on 1/10/2017. mvc-mvc模块启动器
 */
public class WebIocInit {

  /**
   * 默认只载入根目录下的配置文件
   */
  public static WebApplicationContext init() {

    String configLocation = "/";
    try {
      return new WebApplicationContext(configLocation);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

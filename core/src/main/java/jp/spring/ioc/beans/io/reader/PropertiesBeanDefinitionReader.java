package jp.spring.ioc.beans.io.reader;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.PropertiesResourceLoader;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader {

  private Properties configProperties;

  public PropertiesBeanDefinitionReader() {
  }

  public PropertiesBeanDefinitionReader(ResourceLoader resourceLoader) {
    super(resourceLoader);
  }

  @Override
  public void loadBeanDefinitions(String location) throws Exception {
    if (getResourceLoader() instanceof PropertiesResourceLoader) {
      Resource[] resources = getResourceLoader().getResource(location);
      Set<String> componentScan = new HashSet<>();
      //TODO IMPROVE IT 临时措施
      componentScan.add("jp.spring");
      configProperties = new Properties();
      for (Resource resource : resources) {
        configProperties.load(resource.getInputStream());
        componentScan.add(configProperties.getProperty("package.scan"));
        //多个properties之间 package.scan 会相互覆盖，所以每载入一次。扫描一次
      }

      for (String strPackage : componentScan) {
        scanComponent(strPackage);
      }
    }
  }

  private void scanComponent(String strPackage) throws Exception {
    AbstractBeanDefinitionReader reader = AnnotationBeanDefinitionReader.getInstance();
    reader.loadBeanDefinitions(strPackage);
    getRegistry().putAll(reader.getRegistry());
  }

  public Properties getConfigProperties() {
    return configProperties;
  }
}

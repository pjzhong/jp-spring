package jp.spring.ioc.beans.io.reader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.beans.io.loader.PropertiesResourceLoader;
import jp.spring.ioc.beans.io.resources.PropertiesResource;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private Properties configProperties;

    public PropertiesBeanDefinitionReader() {}

    public PropertiesBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
      if(getResourceLoader() instanceof PropertiesResourceLoader) {
          Resource[] resources = getResourceLoader().getResource(location);
          Set<String> componentScan = new HashSet<>();
          configProperties = new Properties();
          for(Resource resource : resources) {
              configProperties.load(resource.getInputStream());
              componentScan.add(configProperties.getProperty("package.scan"));
              //多个properties之间 package.scan 会相互覆盖，所以每载入一次。扫描一次
          }

          for(String strPackage : componentScan) {
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

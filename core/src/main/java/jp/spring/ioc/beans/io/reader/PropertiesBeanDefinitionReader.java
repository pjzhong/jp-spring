package jp.spring.ioc.beans.io.reader;

import java.util.List;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.PropertiesResourceLoader;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader {

  private List<String> packages;


  public PropertiesBeanDefinitionReader(ResourceLoader resourceLoader, List<String> packages) {
    super(resourceLoader);
    this.packages = packages;
  }

  @Override
  public void loadBeanDefinitions(String location) throws Exception {
    //TODO refactor this
    if (getResourceLoader() instanceof PropertiesResourceLoader) {
      for (String strPackage : packages) {
        scanComponent(strPackage);
      }
    }
  }

  private void scanComponent(String strPackage) throws Exception {
    AbstractBeanDefinitionReader reader = AnnotationBeanDefinitionReader.getInstance();
    reader.loadBeanDefinitions(strPackage);
    getRegistry().putAll(reader.getRegistry());
  }
}

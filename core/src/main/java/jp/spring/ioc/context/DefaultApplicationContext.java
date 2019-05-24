package jp.spring.ioc.context;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.DefaultBeanFactory;
import jp.spring.ioc.beans.io.loader.PropertiesLoader;
import jp.spring.ioc.beans.io.reader.AbstractBeanDefinitionReader;
import jp.spring.ioc.beans.io.reader.AnnotationBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.scan.FastClassPathScanner;
import jp.spring.ioc.scan.beans.ClassGraph;


/**
 * Created by Administrator on 12/26/2016.
 */
public class DefaultApplicationContext implements ApplicationContext {

  private DefaultBeanFactory beanFactory;

  private String configLocation;


  public DefaultApplicationContext(String configLocation) throws Exception {
    this.beanFactory = new DefaultBeanFactory();
    this.configLocation = configLocation;
    this.refresh();
  }


  public void refresh() throws Exception {
    loadBeanDefinitions();
    loadBeanPostProcessors(beanFactory);
    registerBeanPostProcessors(beanFactory);
    beanFactory.refresh();
  }

  /**
   * 主要作用，与其它模块的beanPostProcess进行结合 beanPostProcess的统一注册格式是：继承beanPostProcessor并标记@Component,
   * 最后放进jp.spring.process包里面
   */
  private void loadBeanPostProcessors(DefaultBeanFactory beanFactory) {
    try {
      AbstractBeanDefinitionReader reader = AnnotationBeanDefinitionReader.getInstance();
      reader.loadBeanDefinitions("jp.spring.process");
      for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : reader.getRegistry()
          .entrySet()) {
        beanFactory
            .registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
      }
    } catch (Exception e) {
      throw new RuntimeException("load beanPostProcess filed", e);
    }
  }

  private void registerBeanPostProcessors(DefaultBeanFactory beanFactory) throws Exception {
    List<BeanPostProcessor> beanPostProcessors = beanFactory
        .getBeansByType(BeanPostProcessor.class);
    for (Object beanPostProcessor : beanPostProcessors) {
      beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
    }
  }

  @Override
  public DefaultBeanFactory getBeanFactory() {
    return beanFactory;
  }

  @Override
  public Object getBean(String name) {
    return beanFactory.getBean(name);
  }

  @Override
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
    beanFactory.registerBeanDefinition(name, beanDefinition);
  }

  private void loadBeanDefinitions() throws Exception {
    List<Properties> properties = PropertiesLoader.getResource(configLocation);
    List<String> scan = properties.stream()
        .map(p -> p.getProperty(ContextCons.SCANNED))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    scan.add(ContextCons.CORE_PACKAGE);
    FastClassPathScanner scanner = new FastClassPathScanner(scan);
    ClassGraph graph = scanner.scan();
    loadBeanDefinitions(graph);

   /* PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(
        new PropertiesResourceLoader());
    reader.loadBeanDefinitions(configLocation);
    Map<String, BeanDefinition> empty = Collections.emptyMap();
    for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : empty.entrySet()) {
      beanFactory
          .registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
    }*/
  }

  private void loadBeanDefinitions(ClassGraph graph) {
    //TODO IMPLEMENT IT
  }
}

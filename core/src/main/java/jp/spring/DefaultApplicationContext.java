package jp.spring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jp.spring.ioc.beans.factory.BeanDefinition;
import jp.spring.ioc.beans.factory.BeanDefinitionBuilder;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.DefaultBeanFactory;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfo;
import jp.spring.ioc.scan.scan.ScanConfig;
import jp.spring.ioc.scan.scan.ScanResult;
import jp.spring.ioc.scan.scan.Scanner;
import jp.spring.ioc.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 默认环境(启动类)
 *
 * @author ZJP
 * @since 2019年05月25日 13:46:22
 **/
public class DefaultApplicationContext implements ApplicationContext {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private DefaultBeanFactory beanFactory;
  private ClassGraph graph;

  public DefaultApplicationContext() throws Exception {
    this.beanFactory = new DefaultBeanFactory();
    this.refresh();
  }

  private void refresh() throws Exception {
    loadBeanDefinitions(beanFactory);
    registerBeanPostProcessors(beanFactory);
    beanFactory.refresh();
  }

  private void registerBeanPostProcessors(DefaultBeanFactory beanFactory) {
    //TODO refactor this
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
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
    beanFactory.registerBeanDefinition(name, beanDefinition);
  }

  private void loadBeanDefinitions(DefaultBeanFactory beanFactory) throws Exception {
    ScanConfig config = scanConfig();
    logger.info("Found package:{}", config.getPackages());
    logger.info("Found loaders:{}", config.getLoaders());

    // 这里是开始点， 全部切换到ClassInfo
    //PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(
    //    new PropertiesResourceLoader(), config.getPackages());
    //reader.loadBeanDefinitions(null);
    //Map<String, BeanDefinition> empty = reader.getRegistry();
    //for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : empty.entrySet()) {
    //  beanFactory
    //      .registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
    //}

    //FastClassPathScanner
    Scanner scanner = new Scanner(config);
    ScanResult result = scanner.call();

    // configuration
    beanFactory.getProperties().putAll(result.getProperties());

    // graph
    ClassGraph graph = result.getClassGraph();
    Set<ClassInfo> infos = graph.getInfoWithAnnotation(Component.class);
    logger.info("Found infos:{}", infos);
    BeanDefinitionBuilder builder = new BeanDefinitionBuilder(this, infos);
    Set<BeanDefinition> definitions = builder.build();
    definitions.forEach(b -> beanFactory.registerBeanDefinition(b.getName(), b));

    this.graph = graph;
  }

  /**
   * Only two packages need be scan
   *
   * 1.where the DefaultApplicationContext is
   *
   * 2.The caller of DefaultApplicationContext but not  in the same package as
   * DefaultApplicationContext
   *
   * @since 2019年05月25日 15:47:31
   */
  private ScanConfig scanConfig() {
    List<String> white = new ArrayList<>();
    Set<ClassLoader> loaders = new HashSet<>();

    String core = DefaultApplicationContext.class.getPackage().getName();
    white.add(core);
    loaders.add(DefaultApplicationContext.class.getClassLoader());
    // Get the caller
    CallerResolver resolver = new CallerResolver();
    Class<?>[] callStack = resolver.getClassContext();
    for (Class<?> c : callStack) {
      String pkg = c.getPackage().getName();
      if (!pkg.startsWith(core)) {
        white.add(pkg);
        loaders.add(c.getClassLoader());
        break;
      }
    }
    return new ScanConfig(white, loaders);
  }

  private static final class CallerResolver extends SecurityManager {

    @Override
    protected Class<?>[] getClassContext() {
      return super.getClassContext();
    }
  }
}

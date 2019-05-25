package jp.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.DefaultBeanFactory;
import jp.spring.ioc.beans.io.loader.PropertiesResourceLoader;
import jp.spring.ioc.beans.io.reader.AbstractBeanDefinitionReader;
import jp.spring.ioc.beans.io.reader.AnnotationBeanDefinitionReader;
import jp.spring.ioc.beans.io.reader.PropertiesBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.scan.FastClassPathScanner;
import jp.spring.ioc.scan.scan.ScanResult;
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
    //TODO refactor this
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
  public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
    beanFactory.registerBeanDefinition(name, beanDefinition);
  }

  private void loadBeanDefinitions() throws Exception {
    List<String> target = getScanPackage();
    logger.info("Found package:{}", target);

    PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(
        new PropertiesResourceLoader(), target);
    reader.loadBeanDefinitions(configLocation);
    Map<String, BeanDefinition> empty = reader.getRegistry();
    for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : empty.entrySet()) {
      beanFactory
          .registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
    }

    //FastClassPathScanner
    FastClassPathScanner scanner = new FastClassPathScanner(target);
    ScanResult result = scanner.scan();
    beanFactory.getProperties().putAll(result.getProperties());
  }

  /**
   * Only two packages need be scan
   *
   * 1.where the DefaultApplicationContext is
   *
   * 2.and the caller of DefaultApplicationContext but not  in the same package as
   * DefaultApplicationContext
   *
   * @since 2019年05月25日 15:47:31
   */
  private List<String> getScanPackage() {
    List<String> res = new ArrayList<>();
    String corePackage = DefaultApplicationContext.class.getPackage().getName();
    res.add(corePackage);
    // Get the caller
    CallerResolver resolver = new CallerResolver();
    final Class<?>[] callStack = resolver.getClassContext();
    for (int fcsIdx = callStack.length - 1; fcsIdx >= 0; --fcsIdx) {
      String pkg = callStack[fcsIdx].getPackage().getName();
      if (!pkg.startsWith(corePackage)) {
        res.add(pkg);
        break;
      }
    }
    return res;
  }

  private static final class CallerResolver extends SecurityManager {

    @Override
    protected Class<?>[] getClassContext() {
      return super.getClassContext();
    }
  }
}

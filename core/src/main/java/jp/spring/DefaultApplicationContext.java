package jp.spring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.factory.BeanDefinition;
import jp.spring.ioc.factory.BeanDefinitionBuilder;
import jp.spring.ioc.factory.DefaultBeanFactory;
import jp.spring.ioc.scan.ScanConfig;
import jp.spring.ioc.scan.ScanResult;
import jp.spring.ioc.scan.Scanner;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfo;
import jp.spring.util.TypeUtil;
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

  public DefaultApplicationContext() {
    this.beanFactory = new DefaultBeanFactory();
    this.refresh();
  }

  private void refresh() {
    loadBeanDefinitions(beanFactory);
    beanFactory.registerDependency(TypeUtil.simpleName(this.getClass()), this);
    beanFactory.refresh();
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
  public void registerDependency(String name, Object autowiredValue) {
    beanFactory.registerDependency(name, autowiredValue);
  }

  @Override
  public void registerBeanDefinition(BeanDefinition definition) {
    beanFactory.registerBeanDefinition(definition);
  }

  @Override
  public void close() {
    beanFactory.close();
  }


  private void loadBeanDefinitions(DefaultBeanFactory beanFactory) {
    ScanConfig config = scanConfig();
    logger.info("Found package:{}", config.getPackages());
    logger.info("Found loaders:{}", config.getLoaders());

    //FastClassPathScanner
    Scanner scanner = new Scanner(config);
    ScanResult result = scanner.call();

    // configuration
    beanFactory.getProperties().putAll(result.getProperties());

    // graph
    ClassGraph graph = result.getClassGraph();

    Set<ClassInfo> infos = graph.getInfoWithAnnotation(Component.class);
    infos.forEach(f -> logger.info("Found info:{}", f));
    BeanDefinitionBuilder builder = new BeanDefinitionBuilder(this, infos);
    Set<BeanDefinition> definitions = builder.build();
    definitions.forEach(beanFactory::registerBeanDefinition);
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

    String core = DefaultApplicationContext.class.getPackage().getName();
    white.add(core);

    // Get the caller packager
    CallerResolver resolver = new CallerResolver();
    Class<?>[] callStack = resolver.getClassContext();
    for (Class<?> c : callStack) {
      String pkg = c.getPackage().getName();
      if (!pkg.startsWith(core)) {
        white.add(pkg);
        break;
      }
    }

    Set<ClassLoader> loaders = findEvnClassLoader(resolver);
    return new ScanConfig(white, loaders);
  }

  private Set<ClassLoader> findEvnClassLoader(CallerResolver resolver) {
    Set<ClassLoader> uniqueClassLoaders = new LinkedHashSet<>();

    final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    if (systemClassLoader != null) {
      uniqueClassLoaders.add(systemClassLoader);
    }

    //get callerClassLoader
    final Class<?>[] callStack = resolver.getClassContext();
    final String fcsPkgPrefix = DefaultApplicationContext.class.getPackage().getName() + ".";
    for (int fcsIdx = callStack.length - 1; fcsIdx >= 0; --fcsIdx) {
      if (!callStack[fcsIdx].getName().startsWith(fcsPkgPrefix)) {
        uniqueClassLoaders.add(callStack[fcsIdx].getClassLoader());
        break;
      }
    }

    //Get context classloader
    final ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
    if (threadClassLoader != null) {
      uniqueClassLoaders.add(threadClassLoader);
    }

    final Set<ClassLoader> ancestralClassLoaders = new HashSet<>(uniqueClassLoaders.size());
    for (ClassLoader classLoader : uniqueClassLoaders) {
      for (ClassLoader cl = classLoader.getParent(); cl != null; cl = cl.getParent()) {
        ancestralClassLoaders.add(cl);
      }
    }

    uniqueClassLoaders.removeAll(ancestralClassLoaders);
    return uniqueClassLoaders;
  }

  private static final class CallerResolver extends SecurityManager {

    @Override
    protected Class<?>[] getClassContext() {
      return super.getClassContext();
    }
  }
}

package jp.spring.ioc.beans.io.reader;

import java.util.HashMap;
import java.util.Map;
import jp.spring.ioc.beans.io.BeanDefinitionReader;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.support.BeanDefinition;

/**
 * 从配置中读取BeanDefinitionReader
 *
 * @author yihua.huang@dianping.com
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

  private Map<String, BeanDefinition> registry;

  private ResourceLoader resourceLoader;

  protected AbstractBeanDefinitionReader() {
    this(null);
  }

  protected AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
    this.registry = new HashMap<>();
    this.resourceLoader = resourceLoader;
  }

  public Map<String, BeanDefinition> getRegistry() {
    return registry;
  }

  public ResourceLoader getResourceLoader() {
    return resourceLoader;
  }

  @Override
  public BeanDefinition loadBeanDefinition(Class<?> beanClass) {
    return null;//Simply do nothing
  }
}

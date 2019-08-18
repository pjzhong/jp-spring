package jp.spring;

import jp.spring.ioc.factory.BeanFactory;
import jp.spring.ioc.factory.DefaultBeanFactory;

/**
 * @author yihua.huang@dianping.com
 */
public interface ApplicationContext extends BeanFactory {

  DefaultBeanFactory getBeanFactory();
}

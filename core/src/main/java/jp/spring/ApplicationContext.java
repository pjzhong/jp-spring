package jp.spring;

import jp.spring.ioc.factory.DefaultBeanFactory;
import jp.spring.ioc.factory.BeanFactory;

/**
 * @author yihua.huang@dianping.com
 */
public interface ApplicationContext extends BeanFactory {

  DefaultBeanFactory getBeanFactory();
}

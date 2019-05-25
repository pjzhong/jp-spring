package jp.spring;

import jp.spring.ioc.beans.factory.DefaultBeanFactory;
import jp.spring.ioc.beans.factory.BeanFactory;

/**
 * @author yihua.huang@dianping.com
 */
public interface ApplicationContext extends BeanFactory {

  DefaultBeanFactory getBeanFactory();
}

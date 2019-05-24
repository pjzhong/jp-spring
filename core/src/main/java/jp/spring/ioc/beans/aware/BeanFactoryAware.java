package jp.spring.ioc.beans.aware;

import jp.spring.ioc.beans.factory.BeanFactory;

/**
 * Created by Administrator on 12/27/2016.
 */
public interface BeanFactoryAware {

  void setBeanFactory(BeanFactory beanFactory);
}

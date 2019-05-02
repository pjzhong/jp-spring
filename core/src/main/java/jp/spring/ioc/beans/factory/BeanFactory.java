package jp.spring.ioc.beans.factory;

import jp.spring.ioc.beans.support.BeanDefinition;

/**
 * Created by Administrator on 12/24/2016.
 */
public interface BeanFactory {

    Object getBean(String name);

    void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception;
}

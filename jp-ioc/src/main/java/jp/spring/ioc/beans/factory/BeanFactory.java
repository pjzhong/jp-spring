package jp.spring.ioc.beans.factory;

import jp.spring.ioc.beans.support.BeanDefinition;

/**
 * Created by Administrator on 12/24/2016.
 */
public interface BeanFactory {

    public Object getBean(String name) throws Exception;

    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception;
}

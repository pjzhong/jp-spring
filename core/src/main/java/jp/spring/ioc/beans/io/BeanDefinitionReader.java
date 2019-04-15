package jp.spring.ioc.beans.io;

import jp.spring.ioc.beans.support.BeanDefinition;

/**
 * 从配置中读取BeanDefinitionReader
 * @author yihua.huang@dianping.com
 */
public interface BeanDefinitionReader {

    void loadBeanDefinitions(String location) throws Exception;

    BeanDefinition loadBeanDefinition(Class<?> beanClass);
}

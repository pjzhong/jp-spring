package jp.spring.ioc.beans.io.reader;

/**
 * 从配置中读取BeanDefinitionReader
 * @author yihua.huang@dianping.com
 */
public interface BeanDefinitionReader {

    void loadBeanDefinitions(String location) throws Exception;
}

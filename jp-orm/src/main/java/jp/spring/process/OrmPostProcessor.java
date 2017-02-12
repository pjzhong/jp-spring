package jp.spring.process;

import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.stereotype.Component;

/**
 * Created by Administrator on 2/11/2017.
 */
public class OrmPostProcessor implements BeanPostProcessor {

    @Value("jdbc.url")
    private String url;

    @Value("jdbc.driver")
    private String jdbcDriver;

    @Value("jdbc.user")
    private String user;

    @Value("jdbc.password")
    private String password;

    @Override
    public void postProcessBeforeInitialization() throws Exception {

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return null;
    }
}

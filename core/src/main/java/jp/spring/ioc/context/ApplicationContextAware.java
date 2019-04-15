package jp.spring.ioc.context;

import jp.spring.ioc.BeansException;

/**
 * Created by Administrator on 1/3/2017.
 */
public interface ApplicationContextAware {

    void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
}

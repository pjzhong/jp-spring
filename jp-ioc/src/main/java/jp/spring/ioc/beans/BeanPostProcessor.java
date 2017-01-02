package jp.spring.ioc.beans;

/**
 * Created by Administrator on 12/27/2016.
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception;

    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}

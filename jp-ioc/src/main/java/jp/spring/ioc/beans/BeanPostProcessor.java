package jp.spring.ioc.beans;

/**
 * Created by Administrator on 12/27/2016.
 * beanPostProcessor大多数在ApplicationContext完成注册
 * 没有相处什么好办法，只能出此下策
 */
public interface BeanPostProcessor {

    Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception;

    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}

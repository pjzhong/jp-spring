package jp.spring.ioc.beans.factory;


/**
 * ioc容器在初始化和创建bean对外暴露的接口
 * 其它模块如果需要对ioc注册自己的beanPostProcessor。
 * 只需要这样做：
 * 创建一个class继承beanPostProcessor标记上component，创建jp.spring.process包并放在里面。
 * 这样容器在启动的时候就会去加载。
 * */
public interface BeanPostProcessor {

    /**
     * 对某一类的bean进行特定的初始化，例如controller, aspect
     * */
    void postProcessBeforeInitialization() throws Exception;

    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}

package jp.spring.aop.helper;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.impl.AspectBeanPostProcessor;
import jp.spring.aop.impl.ExecutionAspectProxy;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.stereotype.Aspect;
import jp.spring.ioc.util.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 1/22/2017.
 */
public class AspectHelper {

    private static AspectHelper helper= null;

    private AspectHelper() {}

    public static AspectHelper getInstance() {
        if(helper == null) {
            synchronized (AspectHelper.class) {
                if(helper == null) {
                    helper = new AspectHelper();
                }
            }
        }
        return helper;
    }

    /**
     * 以前初始化的代码是放在webmvc里面，但这样不太好。
     * 所以创建了，这个类。
     * 以后对aop初始化工作都会通过这个类完成，尽量减少对外暴露内部的情况
     */
    public void initAspect(AbstractBeanFactory beanFactory) throws Exception{
        List<String> beanNames = beanFactory.getBeanNamByAnnotation(Aspect.class);

        BeanDefinition beanDefinition;
        for(String name : beanNames) {
            beanDefinition = new BeanDefinition();
            BaseAspect aspect = new ExecutionAspectProxy(beanFactory.getType(name), beanFactory.getBean(name));

            beanDefinition.setBeanClass(aspect.getClass());
            beanDefinition.setBean(aspect);

            beanFactory.registerBeanDefinition(name + ".proxy", beanDefinition);
        }

        //注册Aspect处理器
        beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(AspectBeanPostProcessor.class);
        beanFactory.registerBeanDefinition(StringUtils.lowerFirst(AspectBeanPostProcessor.class.getSimpleName()), beanDefinition);
    }
}

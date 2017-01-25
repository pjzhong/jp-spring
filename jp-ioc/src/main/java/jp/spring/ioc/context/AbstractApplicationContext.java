package jp.spring.ioc.context;

import jp.spring.ioc.beans.factory.BeanPostProcessor;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.io.BeanDefinitionReader;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.beans.io.reader.AbstractBeanDefinitionReader;
import jp.spring.ioc.beans.io.reader.AnnotationBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.impl.ApplicationContextAwareProcessor;

import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 12/26/2016.
 */
public abstract  class AbstractApplicationContext  implements ApplicationContext{
    protected AbstractBeanFactory beanFactory;

    public AbstractApplicationContext(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void refresh() throws Exception {
        loadBeanDefinitions(beanFactory);
        loadBeanPostProcessors(beanFactory);
        registerBeanPostProcessors(beanFactory);
        beanFactory.refresh();
    }

    protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception;

    /**
     * 主要作用，与其它模块的beanPostProcess进行结合
     * beanPostProcess的统一注册格式是：继承beanPostProcessor并标记@Component, 最后放进jp.spring.process包里面
     * */
    protected void loadBeanPostProcessors(AbstractBeanFactory beanFactory) {
        try {
            AbstractBeanDefinitionReader reader = AnnotationBeanDefinitionReader.getInstance();
            reader.loadBeanDefinitions("jp.spring.process");
            for(Map.Entry<String, BeanDefinition> beanDefinitionEntry : reader.getRegistry().entrySet()) {
                beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException("load beanPostProcess filed", e);
        }
    }

    protected void registerBeanPostProcessors(AbstractBeanFactory beanFactory) throws Exception {
        List<BeanPostProcessor> beanPostProcessors = beanFactory.getBeansForType(BeanPostProcessor.class);
        for(Object beanPostProcessor : beanPostProcessors) {
            beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
        }

        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    }

    protected void onRefresh() throws Exception {
    }

    @Override
    public Object getBean(String name) throws Exception {
        return beanFactory.getBean(name);
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
        beanFactory.registerBeanDefinition(name, beanDefinition);
    }
}

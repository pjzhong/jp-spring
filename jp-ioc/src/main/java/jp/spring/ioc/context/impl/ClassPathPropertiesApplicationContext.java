package jp.spring.ioc.context.impl;

import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.impl.AutowireCapableBeanFactory;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.beans.io.loader.PropertiesResourceLoader;
import jp.spring.ioc.beans.io.loader.URLResourceLoader;
import jp.spring.ioc.beans.io.reader.PropertiesBeanDefinitionReader;
import jp.spring.ioc.beans.io.reader.XmlBeanDefinitionReader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.context.AbstractApplicationContext;

import java.util.Map;

/**
 * Created by Administrator on 12/26/2016.
 */
public class ClassPathPropertiesApplicationContext extends AbstractApplicationContext {
    
    private String configLocation;

    public ClassPathPropertiesApplicationContext(String configLocation) throws Exception {
        this(configLocation, new AutowireCapableBeanFactory());
    }

    public ClassPathPropertiesApplicationContext(String configLocation, AbstractBeanFactory beanFactory) throws Exception {
        super(beanFactory);
        this.configLocation = configLocation;
        refresh();
    }

    @Override
    protected void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception {
        PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(new PropertiesResourceLoader());
        reader.loadBeanDefinitions(configLocation);
        for(Map.Entry<String, BeanDefinition> beanDefinitionEntry : reader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
        beanFactory.getProperties().putAll(reader.getConfigProperties());
    }

}

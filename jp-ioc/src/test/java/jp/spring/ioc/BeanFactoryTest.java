package jp.spring.ioc;

import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.factory.AbstractBeanFactory;
import jp.spring.ioc.beans.factory.impl.AutowireCapableBeanFactory;
import jp.spring.ioc.beans.io.loader.URLResourceLoader;
import jp.spring.ioc.beans.io.reader.XmlBeanDefinitionReader;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Administrator on 12/24/2016.
 */
public class BeanFactoryTest {

    @Test
    public void test()  {
        try {
           /* //initialized the Factory
            BeanFactory beanFactory = new AutowireCapableBeanFactory();

            //bean definition
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName("jp.spring.ioc.HelloService");

            //Setting values
            PropertyValues propertyValues = new PropertyValues();
            propertyValues.addPropertyValue(new PropertyValue("text", "Hello PJ_ZHONG!"));
            List<String> test = new ArrayList<>();
            test.add("pj_zhong");
            test.add("jp_zhong");
            propertyValues.addPropertyValue(new PropertyValue("list", test));
            beanDefinition.setPropertyValues(propertyValues);

            //create bean
            beanFactory.registerBeanDefinition("helloWorldService", beanDefinition);

            //obtain bean
            HelloService helloService = (HelloService) beanFactory.getBean("helloWorldService");
            helloService.helloWorld();
            helloService.helloWorlds();*/

            XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new URLResourceLoader());
            xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");

            AbstractBeanFactory beanFactory = new AutowireCapableBeanFactory();

            for(Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
                beanFactory.registerBeanDefinition(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
            }

            //初始化bean
            beanFactory.preInstantiateSingletons();

            // 获取bean
            HelloService helloWorldService = (HelloService) beanFactory.getBean("helloWorldService");
           /* helloWorldService.helloWorld();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

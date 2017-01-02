package jp.spring.ioc.xml;

import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.io.loader.URLResourceLoader;
import jp.spring.ioc.beans.io.reader.XmlBeanDefinitionReader;

import java.util.Map;

/**
 * Created by Administrator on 12/25/2016.
 */
public class XmlBeanDefinitionReaderTest {

    public static void main(String[] args) throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new URLResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("tinyioc.xml");
        Map<String, BeanDefinition> registry = xmlBeanDefinitionReader.getRegistry();
        for(String string : registry.keySet()) {
            System.out.println(registry.get(string));
        }
    }
}

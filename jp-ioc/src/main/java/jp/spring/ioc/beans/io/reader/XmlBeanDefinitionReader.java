package jp.spring.ioc.beans.io.reader;

import jp.spring.ioc.beans.io.Resource;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.AnnotationResourceLoader;
import jp.spring.ioc.beans.support.BeanDefinition;
import jp.spring.ioc.beans.support.BeanReference;
import jp.spring.ioc.beans.support.PropertyValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * Created by Administrator on 12/25/2016.
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        Resource[] resources = getResourceLoader().getResource(location);
        for(Resource resource : resources) {
            doLoadBeanDefinitions(resource.getInputStream());
        }
    }

    protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);

        //parse bean;
        registerBeanDefinitions(doc);
        inputStream.close();
    }

    public void registerBeanDefinitions(Document doc) {
        Element root = doc.getDocumentElement();

        parseBeanDefinitions(root);
    }

    protected void parseBeanDefinitions(Element root) {
        NodeList nodeList = root.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(node instanceof Element) {
                Element element = (Element) node;
                processBeanDefinition(element);
            }
        }
    }

    protected void processBeanDefinition(Element element) {
        if("context:component-scan".equals(element.getTagName())) {
            String basePackage = element.getAttribute("base-package");
            AnnotationBeanDefinitionReader reader = new AnnotationBeanDefinitionReader(new AnnotationResourceLoader());
            try {
                reader.loadBeanDefinitions(basePackage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            getRegistry().putAll(reader.getRegistry());
            return;
        }

        String id = element.getAttribute("id");
        String className = element.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition();
        processProperty(element, beanDefinition);
        beanDefinition.setBeanClassName(className);
        getRegistry().put(id, beanDefinition);
    }

    private void processProperty(Element element, BeanDefinition beanDefinition) {
        NodeList propertyNode = element.getElementsByTagName("property");
        for(int i = 0; i < propertyNode.getLength(); i++) {
            Node node = propertyNode.item(i);
            if(node instanceof Element) {
                Element propertyElement = (Element) node;
                String name = propertyElement.getAttribute("name");
                String value = propertyElement.getAttribute("value");
                if(value != null && value.length() > 0) {
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
                } else {
                    String ref = propertyElement.getAttribute("ref");
                    if(ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <property> element for property for"
                        + name + " must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
                }

            }
        }
    }
}

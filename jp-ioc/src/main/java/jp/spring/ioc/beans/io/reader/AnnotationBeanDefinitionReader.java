package jp.spring.ioc.beans.io.reader;


import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.AnnotationResourceLoader;
import jp.spring.ioc.beans.io.resources.FileResource;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.stereotype.Repository;
import jp.spring.ioc.stereotype.Service;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;

import java.io.File;
import java.lang.annotation.Annotation;

/**
 * Created by Administrator on 1/8/2017.
 */
public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {

    AnnotationBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        if(getResourceLoader() instanceof AnnotationResourceLoader) {
            FileResource[] fileResources = (FileResource[]) getResourceLoader().getResource(location);
            for(FileResource fileResource : fileResources) {
                doLoadBeanDefinitions(fileResource);
            }
        }
    }

    protected void doLoadBeanDefinitions(FileResource fileResource) {
        try {
            String className = fileResource.getClassName();
            Class<?> beanClass = AnnotationBeanDefinitionReader.class.getClassLoader().loadClass(className);

            if(JpUtils.isAnnotated(beanClass, Component.class)) {
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(beanClass);

                String name = determinedName(beanClass);
                getRegistry().put(name, beanDefinition);
            }
        } catch (ClassNotFoundException e) {
            //Nothing should do now - 2017-1-8
        }
    }

    private String determinedName(Class<?> beanClass) {
        Annotation[] annotations = beanClass.getAnnotations();

        for(Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            if(type == Controller.class
                    || type == Service.class
                    || type == Repository.class) {
                try {
                    return  (String) JpUtils.findMethod(type, "value").invoke(annotation, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
        }

        return null;
    }
}

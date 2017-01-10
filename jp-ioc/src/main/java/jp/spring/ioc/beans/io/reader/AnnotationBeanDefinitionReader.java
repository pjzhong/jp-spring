package jp.spring.ioc.beans.io.reader;


import jp.spring.ioc.beans.Autowireds;
import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.AnnotationResourceLoader;
import jp.spring.ioc.beans.io.resources.FileResource;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.stereotype.Repository;
import jp.spring.ioc.stereotype.Service;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
                String name = determinedName(beanClass);
                if(StringUtils.isEmpty(name)) {
                    name = StringUtils.lowerFirst(beanClass.getSimpleName());
                }

                Autowireds autowireds = parseAutowired(beanClass);

                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(beanClass);
                beanDefinition.setAutowireds(autowireds);
                getRegistry().put(name, beanDefinition);
            }
        } catch (ClassNotFoundException e) {
            //Nothing should do now - 2017-1-8
        }
    }

    protected Autowireds parseAutowired(Class<?> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();
        Autowireds autowireds = new Autowireds();
        jp.spring.ioc.beans.Autowired autowired;
        for(Field field : fields) {
            if(JpUtils.isAnnotated(field, Autowired.class)) {
                String id = StringUtils.lowerFirst( field.getType().getSimpleName());
                boolean isRequired = field.getAnnotation(Autowired.class).required();

                autowired = new jp.spring.ioc.beans.Autowired(id, field);
                autowired.setRequired(isRequired);

                autowireds.addAutowired(autowired);
            }
        }

        return autowireds;
    }

    private String determinedName(Class<?> beanClass) {
        Annotation[] annotations = beanClass.getAnnotations();

        for(Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            if(type == Controller.class
                    || type == Service.class
                    || type == Repository.class) {
                try {
                    Method  method = JpUtils.findMethod(type, "value");
                    if(method != null) {
                        return (String) method.invoke(annotation, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
        return null;
    }
}

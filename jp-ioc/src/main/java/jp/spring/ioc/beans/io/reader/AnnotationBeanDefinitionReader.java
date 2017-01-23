package jp.spring.ioc.beans.io.reader;


import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.factory.annotation.Qualifier;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.beans.io.resources.ClassResource;
import jp.spring.ioc.beans.support.*;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.stereotype.Repository;
import jp.spring.ioc.stereotype.Service;
import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/8/2017.
 */
public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {

    AnnotationBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String strLocation) throws Exception {
        if(getResourceLoader() instanceof ClassResourceLoader) {

            String[] locations = strLocation.split(";");
            for(String location : locations) {
                ClassResource[] classResources = (ClassResource[]) getResourceLoader().getResource(location);
                for(ClassResource classResource : classResources) {
                    doLoadBeanDefinitions(classResource);
                }
            }
        }
    }

    protected void doLoadBeanDefinitions(ClassResource classResource) {
        try {
            String className = classResource.getClassName();
            Class<?> beanClass = AnnotationBeanDefinitionReader.class.getClassLoader().loadClass(className);

            if(JpUtils.isAnnotated(beanClass, Component.class)) {
                String name = determinedName(beanClass);
                if(StringUtils.isEmpty(name)) {
                    name = StringUtils.lowerFirst(beanClass.getSimpleName());
                }
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(beanClass);

                parseFields(beanDefinition, beanClass);


                getRegistry().put(name, beanDefinition);
            }
        } catch (ClassNotFoundException e) {
            //Simply skip
        }
    }

    protected void parseFields(BeanDefinition beanDefinition, Class<?> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();

        for(Field field : fields) {
            if(JpUtils.isAnnotated(field, Autowired.class)) {
                beanDefinition.add( parseAutowired(field));
            } else if(JpUtils.isAnnotated(field, Value.class)) {
                beanDefinition.add( parseValue(field));
            }
        }
    }

    private InjectField parseAutowired(Field field) {
        InjectField injectField;

        String id = null;
        if(JpUtils.isAnnotated(field, Qualifier.class)) {
          id = field.getAnnotation(Qualifier.class).value();
        }
        boolean isRequired = field.getAnnotation(Autowired.class).required();
        injectField = new InjectField(id, field);
        injectField.setRequired(isRequired);


        return injectField;
    }

    private PropertyValue parseValue(Field field) {
        PropertyValue propertyValue = null;

        Value value =  field.getAnnotation(Value.class);
        String id = value.value();
        if(StringUtils.isEmpty(id)) {
            id = StringUtils.lowerFirst(field.getName());
        }
        boolean isRequired = field.getAnnotation(Value.class).required();

        propertyValue = new PropertyValue();
        propertyValue.setField(field);
        propertyValue.setName(id);
        propertyValue.setRequired(isRequired);

        return propertyValue;
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

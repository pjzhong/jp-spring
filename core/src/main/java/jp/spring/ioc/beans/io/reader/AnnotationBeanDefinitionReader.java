package jp.spring.ioc.beans.io.reader;


import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.factory.annotation.Qualifier;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.beans.io.resources.ClassResource;
import jp.spring.ioc.beans.support.*;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.IocUtil;
import jp.spring.ioc.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 1/8/2017.
 */
public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {

    private AnnotationBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    public static AnnotationBeanDefinitionReader getInstance() {
        return new AnnotationBeanDefinitionReader(new ClassResourceLoader());
    }

    @Override
    public void loadBeanDefinitions(String strLocation) throws Exception {
        if(StringUtils.isEmpty(strLocation)) {
            return;
        }

        if(getResourceLoader() instanceof ClassResourceLoader) {
            String[] locations = strLocation.split("\\s*;\\s*");
            for(String location : locations) {
                ClassResource[] classResources = (ClassResource[]) getResourceLoader().getResource(location);
                for(ClassResource classResource : classResources) {
                    doLoadBeanDefinitions(classResource);
                }
            }
        }
    }

    @Override
    public BeanDefinition loadBeanDefinition(Class<?> beanClass) {
        return parseClass(beanClass);
    }

    protected void doLoadBeanDefinitions(ClassResource classResource) {
        try {
            String className = classResource.getClassName();
            Class<?> beanClass = AnnotationBeanDefinitionReader.class.getClassLoader().loadClass(className);
            parseClass(beanClass);
        } catch (ClassNotFoundException e) {
            //Simply skip
        }
    }

    protected BeanDefinition parseClass(Class<?> beanClass) {
        BeanDefinition definition = null;
        if(IocUtil.isAnnotated(beanClass, Component.class)) {
            definition = new BeanDefinition();
            definition.setBeanClass(beanClass);

            parseFields(definition, beanClass);

            String name = determinedName(beanClass);
            getRegistry().put(name, definition);
        }

        return definition;
    }

    /**
     * 获取户提供的名字， 没有就用是首字母字母小写的简单类名(beanClass.getSimpleName())
     * */
    private String determinedName(Class<?> beanClass) {
        Annotation[] annotations = beanClass.getAnnotations();

        String name = null;
        for(Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            if(IocUtil.isAnnotated(type, Component.class)) {
                try {
                    Method  method = IocUtil.findMethod(type, "value");
                    if(method != null) {
                        method.setAccessible(true);
                        name = (String) method.invoke(annotation, null);
                    }
                    break;
                } catch (Exception e) {
                    break;
                }
            }
        }

        if(StringUtils.isEmpty(name)) {
            name = StringUtils.lowerFirst(beanClass.getSimpleName());
        }
        return name;
    }

    protected void parseFields(BeanDefinition beanDefinition, Class<?> beanClass) {
        Field[] fields = beanClass.getDeclaredFields();

        if(!IocUtil.isEmpty(fields)) {
            for(Field field : fields) {
                if(IocUtil.isAnnotated(field, Autowired.class)) {
                    beanDefinition.add( parseAutowired(field));
                } else if(IocUtil.isAnnotated(field, Value.class)) {
                    beanDefinition.add( parseValue(field));
                }
            }
        }


        if(beanClass.getSuperclass() != null) {
            parseFields(beanDefinition, beanClass.getSuperclass());
        }
    }

    private InjectField parseAutowired(Field field) {
        InjectField injectField;

        String id = null;
        if(IocUtil.isAnnotated(field, Qualifier.class)) {//用户有提供id，没有就让id的属性为空
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

}

package jp.spring.ioc.beans.io.reader;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.factory.annotation.Qualifier;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.beans.io.ResourceLoader;
import jp.spring.ioc.beans.io.loader.ClassResourceLoader;
import jp.spring.ioc.beans.io.resources.ClassResource;
import jp.spring.ioc.beans.BeanDefinition;
import jp.spring.ioc.beans.InjectField;
import jp.spring.ioc.beans.PropertyValue;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
    if (StringUtils.isBlank(strLocation)) {
      return;
    }

    if (getResourceLoader() instanceof ClassResourceLoader) {
      String[] locations = strLocation.split("\\s*;\\s*");
      for (String location : locations) {
        ClassResource[] classResources = (ClassResource[]) getResourceLoader()
            .getResource(location);
        for (ClassResource classResource : classResources) {
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
      Class<?> beanClass = AnnotationBeanDefinitionReader.class.getClassLoader()
          .loadClass(className);
      parseClass(beanClass);
    } catch (ClassNotFoundException e) {
      //Simply skip
    }
  }

  protected BeanDefinition parseClass(Class<?> beanClass) {
    BeanDefinition definition = null;
    if (TypeUtil.isAnnotated(beanClass, Component.class)) {
      definition = new BeanDefinition();
      definition.setClazz(beanClass);

      parseFields(definition, beanClass);

      String name = determinedName(beanClass);
      getRegistry().put(name, definition);
    }

    return definition;
  }

  /**
   * 获取户提供的名字， 没有就用是首字母字母小写的简单类名(beanClass.getSimpleName())
   */
  private String determinedName(Class<?> beanClass) {
    Annotation[] annotations = beanClass.getAnnotations();

    String name = null;
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> type = annotation.annotationType();
      if (TypeUtil.isAnnotated(type, Component.class)) {
        try {
          Method method = TypeUtil.findMethod(type, "value");
          if (method != null) {
            method.setAccessible(true);
            name = (String) method.invoke(annotation, ArrayUtils.EMPTY_OBJECT_ARRAY);
          }
          break;
        } catch (Exception e) {
          break;
        }
      }
    }

    if (StringUtils.isBlank(name)) {
      name = StringUtils.uncapitalize(beanClass.getSimpleName());
    }
    return name;
  }

  protected void parseFields(BeanDefinition beanDefinition, Class<?> beanClass) {
    Field[] fields = beanClass.getDeclaredFields();

    if (!TypeUtil.isEmpty(fields)) {
      for (Field field : fields) {
        if (TypeUtil.isAnnotated(field, Autowired.class)) {
          beanDefinition.add(parseAutowired(field));
        } else if (TypeUtil.isAnnotated(field, Value.class)) {
          beanDefinition.add(parseValue(field));
        }
      }
    }

    if (beanClass.getSuperclass() != null) {
      parseFields(beanDefinition, beanClass.getSuperclass());
    }
  }

  private InjectField parseAutowired(Field field) {
    InjectField injectField;

    String id = null;
    if (TypeUtil.isAnnotated(field, Qualifier.class)) {//用户有提供id，没有就让id的属性为空
      id = field.getAnnotation(Qualifier.class).value();
    }
    injectField = new InjectField(id, field, field.getAnnotation(Autowired.class));

    return injectField;
  }

  private PropertyValue parseValue(Field field) {
    return new PropertyValue(field, field.getAnnotation(Value.class));
  }

}

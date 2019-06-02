package jp.spring.ioc.beans.factory;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jp.spring.ApplicationContext;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.beans.factory.annotation.Qualifier;
import jp.spring.ioc.beans.factory.annotation.Value;
import jp.spring.ioc.scan.beans.ClassInfo;
import jp.spring.ioc.scan.beans.FieldInfo;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BEAN构造者
 *
 * @author ZJP
 * @since 2019年05月31日 20:23:42
 **/
public class BeanDefinitionBuilder {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private ApplicationContext context;
  private Set<ClassInfo> infos;

  public BeanDefinitionBuilder(ApplicationContext context,
      Set<ClassInfo> infos) {
    this.context = context;
    this.infos = infos;
  }

  private Class<?> loadClass(String name) throws ClassNotFoundException {
    return context.getClass().getClassLoader()
        .loadClass(name);
  }

  public Set<BeanDefinition> build() {
    Set<BeanDefinition> res = new HashSet<>();

    for (ClassInfo info : infos) {
      try {
        Class<?> beanClass = loadClass(info.getClassName());
        BeanDefinition definition = parseClass(beanClass, info);
        if (definition != null) {
          res.add(definition);
        }
      } catch (Exception e) {
        logger.error("load {} error, e:{}", info.getClassName(), e);
      }
    }

    return res;
  }

  private BeanDefinition parseClass(Class<?> beanClass, ClassInfo info) throws Exception {
    if (!info.hasAnnotation(Component.class)) {
      return null;
    }

    // parseFields(beanClass, info);
    List<InjectField> injects = parseAutowired(beanClass, info);
    List<PropertyValue> values = parseValue(beanClass, info);

    // SuperClass
    Optional<ClassInfo> superOpt = info.getSuperClass().filter(ClassInfo::isScanned);
    while (superOpt.isPresent()) {
      ClassInfo superInfo = superOpt.get();
      Class<?> clazz = loadClass(superInfo.getClassName());
      injects.addAll(parseAutowired(clazz, superInfo));
      values.addAll(parseValue(clazz, superInfo));

      superOpt = superInfo.getSuperClass().filter(ClassInfo::isScanned);
    }
    String name = determinedName(beanClass);
    return new BeanDefinition(name, beanClass, values, injects);
  }

  /**
   * 获取户提供的名字， 没有就用是首字母字母小写的简单类名(beanClass.getSimpleName())
   */
  private String determinedName(Class<?> beanClass) {
    Annotation[] as = beanClass.getAnnotations();

    String name = null;
    for (Annotation annotation : as) {
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

  private List<InjectField> parseAutowired(Class<?> beanClass, ClassInfo info)
      throws NoSuchFieldException {

    List<InjectField> injects = new ArrayList<>();
    for (FieldInfo f : info.getFieldInfos()) {
      if (f.hasAnnotation(Autowired.class)) {
        Field field = beanClass.getDeclaredField(f.getName());
        Autowired autowired = field.getAnnotation(Autowired.class);
        String qualifier = null;
        if (f.hasAnnotation(Qualifier.class)) {//用户有提供id，没有就让id的属性为空
          qualifier = field.getAnnotation(Qualifier.class).value();
        }
        InjectField inject = new InjectField(qualifier, field,
            autowired);

        injects.add(inject);
      }
    }

    return injects;
  }


  private List<PropertyValue> parseValue(Class<?> beanClass, ClassInfo info)
      throws NoSuchFieldException {

    List<PropertyValue> values = new ArrayList<>();
    for (FieldInfo f : info.getFieldInfos()) {
      if (f.hasAnnotation(Value.class)) {
        Field field = beanClass.getDeclaredField(f.getName());
        Value value = field.getAnnotation(Value.class);
        values.add(new PropertyValue(field, value));
      }
    }

    return values;
  }

}

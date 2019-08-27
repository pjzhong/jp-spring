package jp.spring.ioc.factory;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import jp.spring.ApplicationContext;
import jp.spring.ioc.BeansException;
import jp.spring.ioc.annotation.Autowired;
import jp.spring.ioc.annotation.Component;
import jp.spring.ioc.annotation.Qualifier;
import jp.spring.ioc.annotation.Value;
import jp.spring.ioc.scan.beans.ClassInfo;
import jp.spring.util.TypeUtil;
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

    List<InjectField> injects = parseAutowired(beanClass, info);
    List<PropertyValue> values = parseValue(beanClass, info);
    Method postConstruct = parsePostConstruct(beanClass, info);

    Optional<ClassInfo> superOpt = info.getSuperClass().filter(ClassInfo::isScanned);
    while (superOpt.isPresent()) {
      ClassInfo superInfo = superOpt.get();
      Class<?> clazz = loadClass(superInfo.getClassName());
      injects.addAll(parseAutowired(clazz, superInfo));
      values.addAll(parseValue(clazz, superInfo));

      superOpt = superInfo.getSuperClass().filter(ClassInfo::isScanned);
    }
    String name = TypeUtil.determinedName(beanClass);
    return new BeanDefinition(name, beanClass, values, injects, postConstruct);
  }


  /**
   * 解析注入字段
   *
   * @param beanClass 目标类
   * @param info 扫描数据
   * @since 2019年08月18日 21:33:08
   */
  private List<InjectField> parseAutowired(Class<?> beanClass, ClassInfo info) {

    List<InjectField> injects = new ArrayList<>();
    for (Field f : beanClass.getDeclaredFields()) {
      Autowired autowired = TypeUtil.getAnnotation(f, Autowired.class);
      if (autowired != null) {
        String qualifier = null;
        Qualifier qua = TypeUtil.getAnnotation(f, Qualifier.class);
        if (qua != null) {//用户有提供id，没有就让id的属性为空
          qualifier = qua.value();
        }
        InjectField inject = new InjectField(qualifier, f,
            autowired);
        injects.add(inject);
      }
    }

    return injects;
  }

  /**
   * 解析配置注入
   *
   * @param beanClass 目标类
   * @param info 扫描数据
   * @since 2019年08月18日 21:32:33
   */
  private List<PropertyValue> parseValue(Class<?> beanClass, ClassInfo info) {
    List<PropertyValue> values = new ArrayList<>();
    for (Field f : beanClass.getDeclaredFields()) {
      Value v = TypeUtil.getAnnotation(f, Value.class);
      if (v != null) {
        values.add(new PropertyValue(f, v));
      }
    }

    return values;
  }

  private Method parsePostConstruct(Class<?> beanClass, ClassInfo info) {
    if (beanClass == null || beanClass == Object.class) {
      return null;
    }

    Method[] methods = beanClass.getMethods();
    List<Method> target = new ArrayList<>();
    for (Method m : methods) {
      if (TypeUtil.isAnnotated(m, PostConstruct.class)) {
        target.add(m);
      }
    }

    if (target.isEmpty()) {
      Class<?> superClazz = beanClass.getSuperclass();
      return parsePostConstruct(superClazz, info);
    } else {
      if (1 < target.size()) {
        throw new BeansException(
            "postConstruct method has more than one in " + info.getClassName());
      }

      Method method = target.get(0);
      if (0 < method.getParameters().length) {
        throw new BeansException(
            "postConstruct method should has zero parameter in " + info.getClassName());
      }
      method.setAccessible(true);
      return method;
    }

  }
}

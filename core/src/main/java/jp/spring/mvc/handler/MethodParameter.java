package jp.spring.mvc.handler;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Controller 参数信息封装
 */
public class MethodParameter {

  private Class<?> type;
  private Map<Class<? extends Annotation>, Annotation> annotations;
  private Filler<Object> converter;

  public MethodParameter(Class<?> type,
      Annotation[] annotation, Filler<Object> converter) {
    this.type = type;
    this.converter = converter;
    this.annotations = ObjectUtils.isEmpty(annotation) ? Collections.emptyMap()
        : Collections.unmodifiableMap(Arrays.stream(annotation).collect(
            Collectors.toMap(Annotation::annotationType, a -> a)));
  }

  public Class<?> getType() {
    return type;
  }

  public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
    return annotations;
  }

  public boolean hasAnnotation(Class<? extends Annotation> clazz) {
    return annotations.containsKey(clazz);
  }

  @SuppressWarnings("unchecked")
  public <A extends Annotation> A getAnnotation(Class<? extends A> clazz) {
    return (A) annotations.get(clazz);
  }

  public Filler<Object> getConverter() {
    return converter;
  }
}

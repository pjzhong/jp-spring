package jp.spring.web.handler.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 封装请求方法的参数的封住
 */
public class RequestMethodParameter {

    private Class<?> type = null;

    private Annotation annotation = null;

    private Method valueMethod = null;

    private boolean isPrimitiveType = false;

    public Method getValueMethod() {
        return valueMethod;
    }

    public void setValueMethod(Method valueMethod) {
        this.valueMethod = valueMethod;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public boolean isHasAnnotation() {
        return annotation != null;
    }

    public boolean isPrimitiveType() {
        return isPrimitiveType;
    }

    public void setPrimitiveType(boolean primitiveType) {
        isPrimitiveType = primitiveType;
    }
}

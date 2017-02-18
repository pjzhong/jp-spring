package jp.spring.web.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 封装请求方法的参数的封住
 */
public class RequestMethodParameter {

    private String name = null;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RequestMethodParameter{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type=").append(type);
        sb.append(", annotation=").append(annotation);
        sb.append('}');
        return sb.toString();
    }
}

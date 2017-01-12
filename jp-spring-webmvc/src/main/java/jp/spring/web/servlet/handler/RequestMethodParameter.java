package jp.spring.web.servlet.handler;

import java.lang.annotation.Annotation;

/**
 * Created by Administrator on 1/12/2017.
 */
public class RequestMethodParameter {

    private Class<?> type = null;

    private Annotation annotation = null;

    private boolean isPrimitiveType = false;

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

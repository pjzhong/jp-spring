package jp.spring.web.support;

import jp.spring.ioc.util.JpUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装请求方法的参数的封住
 */
public class MethodParameter {

    private Method method;

    private String name = null;

    private Class<?> parameterType = null;

    /**Generic Type*/
    private Type type = null;

    private int parameterIndex;

    private Annotation annotation = null;

    private boolean isPrimitiveType = false;

    private Map<String, Class<?>> fieldMap;

    /** Getters and setters **/
    public Class<?> getParameterType() {
        return parameterType;
    }

    public void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public boolean hasAnnotation() {
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

    public int getParameterIndex() {
        return parameterIndex;
    }

    public void setParameterIndex(int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * return a field map
     * field name is key;
     * field type is value;
     * invoke this method this object is a POJO
     * */
    public Map<String, Class<?>> getFieldMap() {
        if(JpUtils.isEmpty(fieldMap) && (!JpUtils.isSimpleTypeArray(parameterType)) ) {
            fieldMap = new HashMap<String, Class<?>>();
            Field[] fields = parameterType.getDeclaredFields();
            for(int i = 0; i < fields.length; i++) {
                fieldMap.put(fields[i].getName(), fields[i].getType());
            }
        }

        return fieldMap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodParameter{");
        sb.append("name='").append(name).append('\'');
        sb.append(", parameterType=").append(parameterType);
        sb.append(", annotation=").append(annotation);
        sb.append('}');
        return sb.toString();
    }
}

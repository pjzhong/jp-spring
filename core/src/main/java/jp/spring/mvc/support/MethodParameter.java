package jp.spring.mvc.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import jp.spring.ioc.util.JpUtils;

/**
 * 封装请求方法的参数的封住
 */
public class MethodParameter {

    private Method method;

    private String name = null;

    private Class<?> parameterType = null;

    /**Generic Type*/
    private Class<?> genericType = null;

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

    public Class<?> getGenericType() {
        return genericType;
    }

    public void setGenericType(Class<?> genericType) {
        this.genericType = genericType;
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
            Class<?> clazz = parameterType;
            while(clazz != null) {
                Field[] fields = clazz.getDeclaredFields();
                for(int i = 0; i < fields.length; i++) {
                    fieldMap.put(fields[i].getName(), fields[i].getType());
                }
                clazz = clazz.getSuperclass();//获取父类的
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

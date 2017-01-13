package jp.spring.ioc.beans;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 1/8/2017.
 */
public class Autowired {

    private boolean isRequired = true;

    private final Field field;

    private final String id;

    public Autowired(String id, Field field) {
        this.id = id;
        this.field = field;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public String getFieldName() {
        return this.field.getName();
    }

    public String getId() {
        return id;
    }

    public void inject(Object target, Object value) throws Exception {
        field.setAccessible(true);
        field.set(target, value);
    }

    public Class<?> getAutowiredType() {
        return field.getType();
    }
}
package jp.spring.ioc.beans.support;

import java.lang.reflect.Field;

/**
 * 用于bean的属性注入
 * @author yihua.huang@dianping.com
 */
public class PropertyValue {

    private  String name;

    private Field field;

    private  Object value;

    private boolean isRequired = false;

    public PropertyValue() {

    }

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyValue that = (PropertyValue) o;

        return name != null ? !name.equals(that.name) : that.name != null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }
}

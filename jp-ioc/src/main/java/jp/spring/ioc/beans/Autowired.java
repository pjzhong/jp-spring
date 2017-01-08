package jp.spring.ioc.beans;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 1/8/2017.
 */
public class Autowired {

    private boolean isReuqired = false;

    private final Field field;

    public Autowired(Field field) {
        this.field = field;
    }

    public boolean isReuqired() {
        return isReuqired;
    }

    public void setReuqired(boolean reuqired) {
        isReuqired = reuqired;
    }

    public String getName() {
        return this.field.getName();
    }

    public Class<?> getType() {
        return this.field.getType();
    }
}

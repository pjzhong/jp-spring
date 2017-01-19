package jp.spring.aop.support;

/**
 * Created by Administrator on 1/19/2017.
 */
public class TargetSource {

    private Class<?> targetClass;

    private Class<?>[] interfaces;

    private Object target;

    public TargetSource(Object target) {
        this.target = target;
        this.targetClass = target.getClass();
        this.interfaces = target.getClass().getInterfaces();
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Object getTarget() {
        return target;
    }
}

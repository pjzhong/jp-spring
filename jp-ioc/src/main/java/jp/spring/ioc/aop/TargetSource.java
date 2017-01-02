package jp.spring.ioc.aop;

/**
 * Created by Administrator on 12/26/2016.
 */
public class TargetSource {

    private final Class<?> targetClass;

    private final Class<?>[] interfaces;

    private final Object target;

    public TargetSource(Object target, Class<?> targetClass, Class<?>... intefaces) {
        this.target = target;
        this.targetClass = targetClass;
        this.interfaces = intefaces;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }
}

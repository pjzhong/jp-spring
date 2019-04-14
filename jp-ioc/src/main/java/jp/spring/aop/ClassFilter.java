package jp.spring.aop;

/**
 * Created by Administrator on 1/18/2017.
 */
public interface ClassFilter {

    boolean match(Class<?> cls);
}

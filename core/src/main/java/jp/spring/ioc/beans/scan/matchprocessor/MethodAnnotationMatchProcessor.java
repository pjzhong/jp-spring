package jp.spring.ioc.beans.scan.matchprocessor;

import java.lang.reflect.Method;
import jp.spring.ioc.beans.scan.beans.ClassInfo;

/**
 * Created by Administrator on 11/4/2017.
 */
@FunctionalInterface
public interface MethodAnnotationMatchProcessor {

  void processMatch(ClassInfo info, Class<?> matchingClass, Method matchingMethod);
}

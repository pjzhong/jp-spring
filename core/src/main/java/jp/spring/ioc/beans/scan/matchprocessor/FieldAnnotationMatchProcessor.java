package jp.spring.ioc.beans.scan.matchprocessor;

import java.lang.reflect.Field;
import jp.spring.ioc.beans.scan.beans.ClassInfo;

/**
 * Created by Administrator on 11/4/2017.
 */
@FunctionalInterface
public interface FieldAnnotationMatchProcessor {

  void processMatch(ClassInfo info, Class<?> matchingClass, Field matchingField);
}
package jp.spring.ioc.scan;


import jp.spring.ioc.scan.beans.ClassInfo;

/**
 * Created by Administrator on 2017/12/28.
 */
@FunctionalInterface
public interface MatchProcessor {

  void processMatch(ClassInfo classInfo, Class<?> target);
}

package jp.spring.ioc.scan.matchprocessor;


import jp.spring.ioc.scan.beans.ClassInfo;

/**
 * Created by Administrator on 2017/12/28.
 */
@FunctionalInterface
public interface MatchProcessor<T> {

  void processMatch(ClassInfo classInfo, Class<T> target);
}

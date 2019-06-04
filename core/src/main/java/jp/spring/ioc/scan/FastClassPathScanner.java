package jp.spring.ioc.scan;

import java.util.ArrayList;
import java.util.List;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfo;
import jp.spring.ioc.scan.scan.ScanConfig;
import jp.spring.ioc.scan.scan.ScanResult;
import jp.spring.ioc.scan.scan.Scanner;


public class FastClassPathScanner {

  private List<ClassMatcher> classMatchers = new ArrayList<>();
  private ScanConfig specification = null;

  public FastClassPathScanner(ScanConfig config) {
    specification = config;
  }

  public ScanResult scan() {
    ScanResult result = new Scanner(specification).call();
    classMatchers.forEach(m -> m.lookForMatches(result.getClassGraph()));
    return result;
  }

  public <T> FastClassPathScanner matchSubClassOf(final Class<T> superClass,
      final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo subClassing : g.getInfoOfClassSubClassOf(superClass)) {
        Class<?> cls = loadClass(subClassing.getClassName());
        processor.processMatch(subClassing, cls);
      }
    });
    return this;
  }

  /**
   * Match all classes that implemented the specific interface, exclude annotation
   */
  public <T> FastClassPathScanner matchClassesImplementing(final Class<T> targetInterface,
      final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo classImplementing : g.getInfoOfClassImplementing(targetInterface)) {
        Class<?> cls = loadClass(classImplementing.getClassName());
        processor.processMatch(classImplementing, cls);
      }
    });
    return this;
  }

  public FastClassPathScanner matchClassesWithAnnotation(final Class<?> annotation,
      final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo classWithAnnotation : g.getInfoWithAnnotation(annotation)) {
        Class cls = loadClass(classWithAnnotation.getClassName());
        processor.processMatch(classWithAnnotation, cls);
      }
    });
    return this;
  }

  public FastClassPathScanner matchClassesWithMethodAnnotation(final Class<?> annotation,
      final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo classWithAnnotation : g.getInfoWithMethodAnnotation(annotation)) {
        Class cls = loadClass(classWithAnnotation.getClassName());
        processor.processMatch(classWithAnnotation, cls);
      }
    });
    return this;
  }

  public FastClassPathScanner matchAllInterface(final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo c : g.getAllInterfaces()) {
        Class cls = loadClass(c.getClassName());
        processor.processMatch(c, cls);

      }
    });

    return this;
  }

  public FastClassPathScanner matchAllStandardClass(final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo c : g.getAllStandardClass()) {
        Class cls = loadClass(c.getClassName());
        processor.processMatch(c, cls);
      }
    });

    return this;
  }

  public FastClassPathScanner matchAllAnnotations(final MatchProcessor processor) {
    addClassMatcher(g -> {
      for (ClassInfo c : g.getAllAnnotations()) {
        Class cls = loadClass(c.getClassName());
        processor.processMatch(c, cls);
      }
    });

    return this;
  }


  private Class<?> loadClass(final String className) {
    try {
      return Class.forName(className, false, ClassLoader.getSystemClassLoader());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private void addClassMatcher(ClassMatcher classMatcher) {
    classMatchers.add(classMatcher);
  }

  @FunctionalInterface
  private interface ClassMatcher {

    void lookForMatches(ClassGraph result);
  }
}

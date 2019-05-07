package jp.spring.scan;

import java.util.List;
import jp.spring.ioc.cycle.service.A;
import jp.spring.ioc.scan.FastClassPathScanner;
import jp.spring.ioc.scan.scanner.ClasspathFinder;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Service;
import org.junit.Test;


public class ClassInDefaultPackage {

  @Test
  public void myScannerTest() {
    for (int i = 0; i < 1; i++) {
      FastClassPathScanner scanner = new FastClassPathScanner("jp.spring")
          .matchClassesImplementing(A.class, (info, c) -> System.out.println(c + " impl A"))
          .matchClassesWithAnnotation(Service.class,
              (info, c) -> System.out.println("Annotated by service| " + c))
          .matchClassesWithAnnotation(Component.class,
              (info, c) -> System.out.println("Annotated by component| " + c));
      scanner.scan();
    }

  }

  @Test
  public void elementFindTest() {
    final List<String> classPathElementStrings = new ClasspathFinder().getRawClassPathStrings();
    classPathElementStrings.forEach(System.out::println);
  }
}

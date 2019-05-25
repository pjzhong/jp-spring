package jp.spring.scan;

import java.util.Collections;
import java.util.List;
import jp.spring.ioc.cycle.service.A;
import jp.spring.ioc.scan.FastClassPathScanner;
import jp.spring.ioc.scan.scan.ClassRelativePath;
import jp.spring.ioc.scan.scan.ClasspathFinder;
import jp.spring.ioc.scan.scan.ScanSpecification;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.stereotype.Service;
import org.junit.Test;


public class ClassInDefaultPackage {

  @Test
  public void myScannerTest() {
    for (int i = 0; i < 1; i++) {
      FastClassPathScanner scanner = new FastClassPathScanner(
          Collections.singletonList("jp.spring"))
          .matchClassesImplementing(A.class, (info, c) -> System.out.println(c + " impl A"))
          .matchClassesWithAnnotation(Service.class,
              (info, c) -> System.out.println("Annotated by service| " + c))
          .matchClassesWithAnnotation(Controller.class,
              (info, c) -> System.out.println("Annotated by controller| " + c))
          .matchClassesWithAnnotation(Component.class,
              (info, c) -> System.out.println("Annotated by component| " + c));
      scanner.scan();
    }

  }

  @Test
  public void elementFindTest() {
    ScanSpecification sepc = new ScanSpecification(Collections.singletonList("jp.spring"));
    final List<String> classPathElementStrings = new ClasspathFinder().getRawClassPathStrings();
    classPathElementStrings.stream()
        .peek(System.out::println)
        .map(ClassRelativePath::new)
        .filter(c -> c.isValidClasspathElement(sepc)).forEach(System.out::println);
  }
}

package jp.spring.scan;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import jp.spring.ioc.cycle.service.A;
import jp.spring.ioc.scan.FastClassPathScanner;
import jp.spring.ioc.scan.scan.ClassRelativePath;
import jp.spring.ioc.scan.scan.ClasspathFinder;
import jp.spring.ioc.scan.scan.ScanConfig;
import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.stereotype.Service;
import org.junit.Test;


public class ClassInDefaultPackage {

  @Test
  public void myScannerTest() {
    for (int i = 0; i < 1; i++) {
      ScanConfig config = new ScanConfig(Collections.singletonList("jp.spring"));
      FastClassPathScanner scanner = new FastClassPathScanner(config)
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
    Set<ClassLoader> loaders = Collections.singleton(getClass().getClassLoader());
    ScanConfig sepc = new ScanConfig(Collections.singletonList("jp.spring"), loaders);
    final List<String> classPathElementStrings = new ClasspathFinder(sepc).getRawClassPathStrings();
    classPathElementStrings.stream()
        .peek(System.out::println)
        .map(ClassRelativePath::new)
        .filter(c -> c.isValidClasspathElement(sepc)).forEach(System.out::println);
  }
}

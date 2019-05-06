package jp.spring.scan;

import jp.spring.ioc.beans.scan.FastClassPathScanner;
import jp.spring.ioc.stereotype.Component;
import org.junit.Test;


public class ClassInDefaultPackage {

  @Test
  public void myScannerTest() {
    FastClassPathScanner scanner = new FastClassPathScanner("com.zjp", "jp.spring", "fai")
        .matchClassesWithAnnotation(Component.class,
            (info, c) -> System.out.println("Annotated by component| " + c));
    scanner.scan();
  }
}

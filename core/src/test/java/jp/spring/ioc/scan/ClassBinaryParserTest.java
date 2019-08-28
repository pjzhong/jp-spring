package jp.spring.ioc.scan;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassBinaryParserTest {

  private ClassFileBinaryParser parser;

  @BeforeEach
  void before() {
    parser = new ClassFileBinaryParser();
  }

  @AfterEach
  void after() {
    parser = null;
  }

  private Optional<ClassInfo> scan(Class<?> clazz) throws IOException {
    InputStream stream = this.getClass().getResourceAsStream(clazz.getSimpleName() + ".class");
    ClassGraph graph = ClassGraph.build(Collections.singleton(parser.parse(stream)));

    return graph.getInfo(clazz.getName());
  }

  @Test
  void interfaceParseTest() throws IOException {
    Class<?> clazz = ExampleInterface.class;
    ClassInfo info = scan(clazz).get();

    assertFalse(info.isStandardClass());
    assertFalse(clazz.isAnnotation() || info.isAnnotation());
    assertTrue(clazz.isInterface() && info.isInterface());

    assertEquals(Object.class.getName(), info.getSuperClass().map(ClassInfo::getName).get());

    commonInfoTest(info, clazz);
  }

  @Test
  void annotationParseTest() throws IOException {
    Class<?> clazz = ExampleAnnotation.class;
    ClassInfo info = scan(clazz).get();

    assertFalse(info.isStandardClass());
    assertTrue(clazz.isAnnotation() && info.isAnnotation());
    assertTrue(clazz.isInterface() && info.isInterface());

    assertEquals(Object.class.getName(), info.getSuperClass().map(ClassInfo::getName).get());

    commonInfoTest(info, clazz);
  }

  @Test
  void classParseTest() throws IOException {
    Class<?> clazz = ExampleClass.class;
    ClassInfo info = scan(clazz).get();

    assertTrue(info.isStandardClass());
    assertFalse(clazz.isAnnotation() || info.isAnnotation());
    assertFalse(clazz.isInterface() || info.isInterface());

    assertEquals(info.getSuperClass().map(ClassInfo::getName).get(), ArrayList.class.getName());

    commonInfoTest(info, clazz);
  }

  private void commonInfoTest(ClassInfo info, Class<?> target) {
    assertEquals(target.getName(), info.getName());
    {
      Class<?>[] interfaces = target.getInterfaces();
      Set<ClassInfo> implemented = info.getImplemented();
      assertEquals(interfaces.length, implemented.size());
      List<String> expectedInterfaces = Stream.of(interfaces).map(Class::getName)
          .collect(toList());
      List<String> actualInterfaces = implemented.stream().map(ClassInfo::getName)
          .collect(toList());
      assertEquals(expectedInterfaces, actualInterfaces);
    }

    {
      Annotation[] annotations = target.getAnnotations();
      Set<ClassInfo> annotating = info.getAnnotations();
      assertEquals(annotations.length, annotating.size());

      List<String> expectedAnnotations = Stream.of(annotations)
          .map(a -> a.annotationType().getName())
          .collect(toList());
      List<String> actualAnnotations = annotating.stream().map(ClassInfo::getName)
          .collect(toList());
      assertEquals(expectedAnnotations, actualAnnotations);
    }
  }

  @Test
  void not_class_test() {
    byte[] s = "Not a class".getBytes(StandardCharsets.UTF_8);
    InputStream stream = new ByteArrayInputStream(s);

    assertThrows(IllegalArgumentException.class, () -> parser.parse(stream),
        "Not a valid class File");
  }

}

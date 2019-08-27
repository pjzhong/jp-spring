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
import java.util.HashMap;
import java.util.List;
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

  @Test
  void interfaceParseTest() throws IOException {
    InputStream stream = this.getClass().getResourceAsStream("ExampleInterface.class");
    ClassInfo info = ClassGraph.build(Collections.singleton(parser.parse(stream)));

    assertFalse(info.isStandardClass());
    assertFalse(info.isAnnotation());
    assertTrue(info.isInterface());

    assertEquals(Object.class.getName(), info.getSuperClass().map(ClassInfo::getName).get());

    commonInfoTest(info, ExampleInterface.class);
  }

  @Test
  void annotationParseTest() throws IOException {
    InputStream stream = this.getClass().getResourceAsStream("ExampleAnnotation.class");
    ClassInfo info = parser.parse(stream).build(new HashMap<>());

    assertFalse(info.isStandardClass());
    assertTrue(info.isAnnotation());
    assertFalse(info.isInterface());

    assertEquals(Object.class.getName(), info.getSuperClass().map(ClassInfo::getName).get());

    commonInfoTest(info, ExampleAnnotation.class);
  }

  @Test
  void classParseTest() throws IOException {
    InputStream stream = this.getClass().getResourceAsStream("ExampleClass.class");
    ClassInfo info = parser.parse(stream).build(new HashMap<>());

    assertTrue(info.isStandardClass());
    assertFalse(info.isAnnotation());
    assertFalse(info.isInterface());

    assertEquals(info.getSuperClass().map(ClassInfo::getName).get(), ArrayList.class.getName());

    commonInfoTest(info, ExampleClass.class);
  }

  private void commonInfoTest(ClassInfo info, Class<?> target) {
    assertEquals(target.getName(), info.getName());
    {
      Class<?>[] interfaces = target.getInterfaces();
      Set<ClassInfo> implemented = info.getImplemented();
      assertEquals(interfaces.length, implemented.size());
      List<String> expectedInterfaces = Stream.of(interfaces).map(Class::getName).sorted()
          .collect(toList());
      List<String> actualInterfaces = implemented.stream().map(ClassInfo::getName).sorted()
          .collect(toList());
      assertEquals(expectedInterfaces, actualInterfaces);
    }

    {
      Annotation[] annotations = target.getAnnotations();
      Set<ClassInfo> annotating = info.getAnnotations();
      assertEquals(annotations.length, annotating.size());

      List<String> expectedAnnotations = Stream.of(annotations)
          .map(a -> a.annotationType().getName())
          .sorted()
          .collect(toList());
      List<String> actualAnnotations = annotating.stream().map(ClassInfo::getName).sorted()
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

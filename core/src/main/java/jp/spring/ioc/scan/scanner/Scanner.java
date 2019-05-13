package jp.spring.ioc.scan.scanner;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;


/**
 * Created by Administrator on 10/28/2017.
 */
public class Scanner implements Callable<ClassGraph> {

  private final ScanSpecification specification;

  public Scanner(ScanSpecification specification) {
    this.specification = specification;
  }

  @Override
  public ClassGraph call() {
    // Get all classpathElements from the runtime-context, have no idea what these is ?
    // Write a class, and run the code below:
    //      System.getProperty("java.class.path");
    final List<String> classPathElementStrings = new ClasspathFinder().getRawClassPathStrings();
    final List<ClassRelativePath> rawClassPathElements = new ArrayList<>();
    for (String classElementStr : classPathElementStrings) {
      rawClassPathElements.add(new ClassRelativePath(classElementStr));
    }

    // split dir and jar file than started to scan them
    long scannedStart = System.currentTimeMillis();
    Map<ClassRelativePath, ClasspathElement<?>> elementMap = new ConcurrentHashMap<>();
    rawClassPathElements.parallelStream()
        .filter(c -> c.isValidClasspathElement(specification))
        .forEach(c -> elementMap.computeIfAbsent(c, this::newClassElement));
    System.out.format("scanned done cost:%s%n", System.currentTimeMillis() - scannedStart);

    //
    // restore the classpathOrder and filtered the same classes but occurs in difference jar file
    // (remove the second and subsequent)
    //
    long maskStart = System.currentTimeMillis();
    List<ClasspathElement<?>> classpathOrder = restoredClasspathOrder(rawClassPathElements,
        elementMap);
    System.out.format("mask done cost:%s%n", System.currentTimeMillis() - maskStart);

    // start to parse the class files found in the runtime context */
    long parseStart = System.currentTimeMillis();
    final ConcurrentLinkedQueue<ClassInfoBuilder> infoBuilders = new ConcurrentLinkedQueue<>();
    ClassFileBinaryParser parser = new ClassFileBinaryParser();
    classpathOrder.parallelStream().forEach(c ->
        c.iterator().forEachRemaining(i -> {
          ClassInfoBuilder builder = null;
          try {
            builder = parser.parse(i);
          } catch (IOException e) {
            e.printStackTrace();
          }
          if (builder != null) {
            infoBuilders.add(builder);
          }
        })
    );
    classpathOrder.forEach(ClasspathElement::close);
    System.out.format(
        "parsed done cost:%s%n", System.currentTimeMillis() - parseStart);

    // build the classGraph in single-thread
    long buildStart = System.currentTimeMillis();
    ClassGraph classGraph = ClassGraph.builder(specification, infoBuilders).build();
    System.out.format(
        "buildGraph cost:%s%n", System.currentTimeMillis() - buildStart);
    return classGraph;
  }

  private ClasspathElement newClassElement(ClassRelativePath relativePath) {
    if (relativePath.isDirectory()) {
      return new ClassPathElementDir(relativePath, specification);
    } else {
      return new ClasspathElementZip(relativePath, specification);
    }
  }

  /**
   * restore the classPath after scanned;
   */
  private List<ClasspathElement<?>> restoredClasspathOrder(List<ClassRelativePath> rawPaths,
      Map<ClassRelativePath, ClasspathElement<?>> elementMap) {
    final List<ClasspathElement<?>> order = new ArrayList<>();
    final Set<String> encounteredClassFile = new HashSet<>();
    for (ClassRelativePath relativePath : rawPaths) {
      ClasspathElement element = elementMap.get(relativePath);
      if (element != null) {
        element.maskFiles(encounteredClassFile);
        if (!element.isEmpty()) {
          order.add(element);
        }
      }
    }

    return order;
  }
}

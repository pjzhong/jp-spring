package jp.spring.ioc.scan.scan;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;


/**
 * Created by Administrator on 10/28/2017.
 */
public class Scanner {

  private final ScanSpecification specification;

  public Scanner(ScanSpecification specification) {
    this.specification = specification;
  }


  public ScanResult call() {
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
    Map<ClassRelativePath, ClasspathElement> elementMap = new ConcurrentHashMap<>();
    rawClassPathElements.stream()
        .filter(c -> c.isValidClasspathElement(specification))
        .forEach(c -> elementMap.computeIfAbsent(c, this::newClassElement));
    System.out.format("scanned done cost:%s%n", System.currentTimeMillis() - scannedStart);

    //
    // restore the classpathOrder
    // TODO
    //  filtered the same classes but occurs in difference file(s)
    //  (remove the second and subsequent), use an other way to do this, give up maskFiles method
    //
    long maskStart = System.currentTimeMillis();
    List<ClasspathElement> classpathOrder = rawClassPathElements.stream()
        .map(elementMap::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    System.out.format("mask done cost:%s%n", System.currentTimeMillis() - maskStart);

    // start to read the files found in the runtime context
    long parseStart = System.currentTimeMillis();
    ClassFileBinaryParser parser = new ClassFileBinaryParser();
    List<ReadResult> results = classpathOrder.parallelStream()
        .map(c -> c.read(parser))// Scanning
        .collect(Collectors.toList());
    classpathOrder.forEach(ClasspathElement::close);
    System.out.format("parsed done cost:%s%n", System.currentTimeMillis() - parseStart);

    // build the classGraph in single-thread
    long buildStart = System.currentTimeMillis();
    List<ClassInfoBuilder> builders = results.stream()
        .map(ReadResult::getBuilders)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    ClassGraph classGraph = ClassGraph.builder(builders).build();
    System.out.format("buildGraph cost:%s%n", System.currentTimeMillis() - buildStart);

    Properties properties = new Properties();
    for (ReadResult result : results) {
      properties.putAll(result.getProperties());
    }
    return ScanResult.of(classGraph, properties);
  }

  private ClasspathElement newClassElement(ClassRelativePath relativePath) {
    if (relativePath.isDirectory()) {
      return new ClassPathElementDir(relativePath, specification);
    } else {
      return new ClasspathElementZip(relativePath, specification);
    }
  }
}
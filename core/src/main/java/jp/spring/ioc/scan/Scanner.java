package jp.spring.ioc.scan;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jp.spring.ioc.scan.beans.ClassGraph;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Administrator on 10/28/2017.
 */
public class Scanner {

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ScanConfig config;

  public Scanner(ScanConfig config) {
    this.config = config;
  }

  public ScanResult call() {
    final List<String> classPathElementStrings = new ClasspathFinder(config)
        .getRawClassPathStrings();
    final List<ClassRelativePath> rawClassPathElements = new ArrayList<>();
    for (String classElementStr : classPathElementStrings) {
      rawClassPathElements.add(new ClassRelativePath(classElementStr));
    }

    // split dir and jar file than started to scan them
    long scannedStart = System.currentTimeMillis();
    Map<ClassRelativePath, ClasspathElement> elementMap = new ConcurrentHashMap<>();
    rawClassPathElements.stream()
        .filter(c -> c.isValidClasspathElement(config))
        .forEach(c -> elementMap.computeIfAbsent(c, this::newClassElement));
    logger.info("scanned done cost:{}", System.currentTimeMillis() - scannedStart);

    // start to read the files found in the runtime context
    long parseStart = System.currentTimeMillis();
    ClassFileBinaryParser parser = new ClassFileBinaryParser();
    List<ReadResult> results = elementMap.values().parallelStream()
        .map(c -> c.read(parser))// Scanning
        .collect(Collectors.toList());
    elementMap.values().forEach(ClasspathElement::close);
    logger.info("parsed done cost:{}", System.currentTimeMillis() - parseStart);

    // build the classGraph in single-thread
    long buildStart = System.currentTimeMillis();
    List<ClassInfoBuilder> builders = results.stream()
        .map(ReadResult::getBuilders)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    ClassGraph classGraph = ClassGraph.builder(builders).build();
    logger.info("buildGraph cost:{}", System.currentTimeMillis() - buildStart);

    Properties properties = new Properties();
    for (ReadResult result : results) {
      properties.putAll(result.getProperties());
    }
    return ScanResult.of(classGraph, properties);
  }

  private ClasspathElement newClassElement(ClassRelativePath relativePath) {
    if (relativePath.isDirectory()) {
      return new ClassPathElementDir(relativePath, config);
    } else {
      return new ClasspathElementZip(relativePath, config);
    }
  }
}

package jp.spring.ioc.scan;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jp.spring.ioc.scan.beans.ClassData;
import jp.spring.ioc.scan.beans.ClassGraph;
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

    final List<ClassRelativePath> relativePaths = findClassPaths(config);

    // split dir and jar file than started to scan them
    Deque<ClassRelativePath> opening = new LinkedList<>(relativePaths);
    Map<ClassRelativePath, ClassPathElement> elements = new ConcurrentHashMap<>();
    while (!opening.isEmpty()) {
      ClassRelativePath path = opening.pollFirst();
      if (!elements.containsKey(path) && path.blockJdk(config)) {
        ClassPathElement element = newClassElement(path);
        if (element != null) {
          try {
            logger.debug("opening element {}", path);
            element.open(opening);
            elements.put(path, element);
          } catch (Exception e) {
            element.close();
          }
        }
      } else {
        logger.debug("ignore {}, because it duplicated or not a valid element", path);
      }
    }

    //open and find new classPathElement

    // start to read the files found in the runtime context
    long parseStart = System.currentTimeMillis();
    ClassFileBinaryParser parser = new ClassFileBinaryParser();
    List<ReadResult> results = elements.values().parallelStream()
        .map(c -> c.scan(parser))// Scanning
        .collect(Collectors.toList());
    elements.values().forEach(ClassPathElement::close);
    logger.info("parsed done cost:{}", System.currentTimeMillis() - parseStart);

    // build the classGraph in single-thread
    long buildStart = System.currentTimeMillis();
    List<ClassData> builders = results.stream()
        .map(ReadResult::getBuilders)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    ClassGraph classGraph = ClassGraph.build(builders);
    logger.info("buildGraph cost:{}", System.currentTimeMillis() - buildStart);

    Properties properties = new Properties();
    for (ReadResult result : results) {
      properties.putAll(result.getProperties());
    }
    return ScanResult.of(classGraph, properties);
  }

  private ClassPathElement newClassElement(ClassRelativePath relativePath) {
    try {
      if (relativePath.isDirectory()) {//If
        return new ClassPathElementDir(relativePath, config);
      } else if (relativePath.isJar()) {
        return new ClasspathElementZip(relativePath, config);
      } else {
        logger.info("ignore unknown element {}", relativePath);
        return null;
      }
    } catch (IOException e) {
      logger.info("create element {} error-{}", relativePath, e.getMessage());
    }
    return null;
  }

  private List<ClassRelativePath> findClassPaths(ScanConfig config) {
    List<String> elementStrs = new ArrayList<>();
    //for convenient, only handler sun.misc.Launcher$AppClassLoader
    ClassLoaderHandler handler = new URLClassLoaderHandler();
    for (ClassLoader loader : config.getLoaders()) {
      try {
        elementStrs.addAll(handler.handle(loader));
      } catch (Exception ignore) {
        //ignore
      }
    }

    final List<ClassRelativePath> relativePaths = new ArrayList<>();
    for (String classElementStr : elementStrs) {
      try {
        relativePaths.add(new ClassRelativePath(classElementStr));
        logger.debug("found element {}", classElementStr);
      } catch (Exception e) {
        logger
            .debug("error on create relative path of {}, Msg-{}", classElementStr, e.getMessage());
      }
    }

    return relativePaths;
  }
}

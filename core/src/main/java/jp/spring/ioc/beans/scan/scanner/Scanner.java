package jp.spring.ioc.beans.scan.scanner;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import jp.spring.ioc.beans.scan.beans.ClassGraph;
import jp.spring.ioc.beans.scan.beans.ClassInfoBuilder;
import jp.spring.ioc.beans.scan.utils.WorkQueue;


/**
 * Created by Administrator on 10/28/2017.
 */
public class Scanner implements Callable<ClassGraph> {

  private final ExecutorService executorService;
  private final ScanSpecification specification;
  private final int workers;
  private final InterruptionChecker interruptionChecker = new InterruptionChecker();

  public Scanner(ExecutorService executorService, int numberThread, ScanSpecification specification,
      FailureHandler failureHandler) {
    this.workers = numberThread;
    this.specification = specification;
    this.executorService = executorService;
  }

  @Override
  public ClassGraph call() throws Exception {
    /**
     * Get all classpathElements from the runtime-context, have no idea what these is ?
     * Write a class, and run the code below:
     *      System.getProperty("java.class.path");
     * */
    final List<String> classPathElementStrings = new ClasspathFinder().getRawClassPathStrings();
    final List<ClassRelativePath> rawClassPathElements = new ArrayList<>();
    for (String classElementStr : classPathElementStrings) {
      rawClassPathElements.add(new ClassRelativePath(classElementStr));
    }

    /**
     * split dir and jar file than started to scan them
     * */
    long scannedStart = System.nanoTime();
    final ClassRelativePathToElementMap elementMap = new ClassRelativePathToElementMap(
        specification, interruptionChecker);
    WorkQueue<ClassRelativePath> relativePathQueue = null;
    try {
      relativePathQueue = new WorkQueue<>(rawClassPathElements, c -> {
        if (elementMap.get(c) != null) {
          //duplication
        } else if (c.isValidClasspathElement(specification)) {
          try {
            elementMap.createSingleton(c);
          } catch (Exception e) {
            System.out.println(e);
          }
        }
      }, interruptionChecker);
      relativePathQueue.start(executorService, workers - 1 /* in case there only one thread*/);
      relativePathQueue.runWorkers();
    } finally {
      if (relativePathQueue != null) {
        relativePathQueue.close();
      }
    }
    System.out.println(
        "scanned done cost:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - scannedStart));

    /**
     * restore the classpathOrder and filtered the same classes but occurs in difference jar file
     * (remove the second and subsequent)
     * */
    long maskStart = System.nanoTime();
    List<ClasspathElement<?>> classpathOrder = restoredClasspathOrder(rawClassPathElements,
        elementMap);
    System.out
        .println("mask done cost:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - maskStart));

    /**
     * start to parse the class files found in the runtime context
     * */
    long parseStart = System.nanoTime();
    final ConcurrentLinkedQueue<ClassInfoBuilder> infoBuilders = new ConcurrentLinkedQueue<>();
    WorkQueue<InputStream> streamWorkQueue = null;
    try {
      ClassFileBinaryParser parser = new ClassFileBinaryParser();
      streamWorkQueue = new WorkQueue<>(
          queue -> classpathOrder.forEach(e -> e.forEach(queue::addWorkUnit)),
          stream -> {
            ClassInfoBuilder builder = parser.parse(stream);
            if (builder != null) {
              infoBuilders.add(builder);
            }
          },
          interruptionChecker
      );
      streamWorkQueue.start(executorService, workers - 1 /* in case there only one thread*/);
      streamWorkQueue.runWorkers();
    } finally {
      if (streamWorkQueue != null) {
        streamWorkQueue.close();
      }
      classpathOrder.forEach(ClasspathElement::close);
    }
    System.out.println(
        "parsed done cost:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - parseStart));

    /**
     * build the classGraph in single-thread
     * */
    long buildStart = System.nanoTime();
    ClassGraph classGraph = ClassGraph.builder(specification, infoBuilders).build();
    System.out.println(
        "buildGraph cost:" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - buildStart));
    return classGraph;
  }

  /**
   * restore the classPath after scanned;
   */
  private List<ClasspathElement<?>> restoredClasspathOrder(List<ClassRelativePath> rawPaths,
      ClassRelativePathToElementMap elementMap)
      throws InterruptedException {
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

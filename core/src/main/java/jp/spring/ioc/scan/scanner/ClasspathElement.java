package jp.spring.ioc.scan.scanner;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;


/**
 * A classpath element (a directory or jarfile on the classpath). leave nestedJar alone first
 *
 * The type Of file, can be File or ZipEntry Object
 *
 * Iterator can iterate through the input of file found in this ClasspathElement
 */
public abstract class ClasspathElement<F> implements AutoCloseable {

  /**
   * The list of whiteList classFiles found within this classpath resource, if scanFiles is true.
   */
  Map<String, F> classFilesMap = Collections.emptyMap();//relativePath , File

  boolean ioExceptionOnOpen;
  ClassRelativePath classRelativePath;

  ClasspathElement(ClassRelativePath classRelativePath) {
    this.classRelativePath = classRelativePath;
  }

  /**
   * remove file encountered file from classFilesMap
   *
   * @param encounteredRelativePath the files has encountered in the run-time context
   */
  public void maskFiles(Set<String> encounteredRelativePath) {
    final Set<String> maskedRelativePaths = new HashSet<>();
    classFilesMap.forEach((relativePath, classResource) -> {
      if (encounteredRelativePath.contains(relativePath)) {
        maskedRelativePaths.add(relativePath);
      } else {
        encounteredRelativePath.add(relativePath);
      }
    });

    maskedRelativePaths.forEach(classFilesMap::remove);
  }

  public boolean isEmpty() {
    return classFilesMap.isEmpty();
  }

  @Override
  public abstract void close();

  public abstract List<ClassInfoBuilder> parse(ClassFileBinaryParser parser);


  @Override
  public String toString() {
    return classRelativePath.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ClasspathElement<?> that = (ClasspathElement<?>) o;

    return Objects.equals(classRelativePath, that.classRelativePath);
  }

  @Override
  public int hashCode() {
    return classRelativePath != null ? classRelativePath.hashCode() : 0;
  }
}

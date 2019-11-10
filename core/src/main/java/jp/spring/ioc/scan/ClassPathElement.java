package jp.spring.ioc.scan;


import java.util.Deque;

/**
 * A classpath element (a directory or jarfile on the classpath). leave nestedJar alone first
 *
 * The type Of file, can be File or ZipEntry Object
 *
 * Iterator can iterate through the input of file found in this ClasspathElement
 */
public interface ClassPathElement extends AutoCloseable {

  void close();

  void open(Deque<ClassRelativePath> elements);

  ReadResult scan(ClassFileBinaryParser parser);
}

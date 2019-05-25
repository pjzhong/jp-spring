package jp.spring.ioc.scan.scan;


/**
 * A classpath element (a directory or jarfile on the classpath). leave nestedJar alone first
 *
 * The type Of file, can be File or ZipEntry Object
 *
 * Iterator can iterate through the input of file found in this ClasspathElement
 */
public interface ClasspathElement extends AutoCloseable {

  void close();

  ReadResult read(ClassFileBinaryParser parser);
}

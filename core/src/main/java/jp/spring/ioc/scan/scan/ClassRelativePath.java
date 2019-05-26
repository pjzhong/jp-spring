package jp.spring.ioc.scan.scan;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import jp.spring.ioc.scan.utils.ScanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/11/3.
 */
public class ClassRelativePath {

  private final String relativePath;
  private final boolean isJar;
  /** The canonical file for the relative path. */
  private File file;

  public ClassRelativePath(String relativePath) {
    this.relativePath = relativePath;
    this.isJar = this.relativePath.contains("!") || ScanUtils.isJar(this.relativePath);
  }


  public boolean isValidClasspathElement(ScanConfig spec) {
    final String path = relativePath;
    if (StringUtils.isBlank(path) || !exists()) {
      return false;
    }

    //ignore non-jar file on class path
    return !isFile() || (!spec.blockJdk(path) && isJar);
  }

  public boolean exists() {
    return asFile().exists();
  }

  public boolean isDirectory() {
    return asFile().isDirectory();
  }

  public boolean isFile() {
    return asFile().isFile();
  }


  public File asFile() {
    if (file == null) {
      file = new File(relativePath);
      try {
        file = file.getCanonicalFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return file;
  }

  @Override
  public String toString() {
    return "ClassRelativePath{" + "relativePath='" + relativePath + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClassRelativePath that = (ClassRelativePath) o;
    return Objects.equals(relativePath, that.relativePath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(relativePath);
  }
}

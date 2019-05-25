package jp.spring.ioc.scan.scan;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import jp.spring.ioc.scan.utils.FastPathResolver;
import jp.spring.ioc.scan.utils.ScanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/11/3.
 */
public class ClassRelativePath {

  private final String relativePath;
  private final boolean isJar;
  private String resolvedPath;
  /**
   * The canonical file for the relative path.
   */
  private File file;

  public ClassRelativePath(String relativePath) {
    this.relativePath = relativePath;
    this.isJar = this.relativePath.contains("!") || ScanUtils.isJar(this.relativePath);
  }


  public boolean isValidClasspathElement(ScanSpecification spec) {
    final String path = getResolvedPath();
    if (StringUtils.isBlank(path) || !exists()) {
      return false;
    }

    //ignore non-jar file on class path
    return !isFile() || (!spec.blockJdk(path) && isJar);
  }

  public boolean exists() {
    try {
      return asFile().exists();
    } catch (IOException e) {
      return false;
    }
  }

  public boolean isDirectory() {
    try {
      return asFile().isDirectory();
    } catch (IOException e) {
      return false;
    }
  }

  public boolean isFile() {
    try {
      return asFile().isFile();
    } catch (IOException e) {
      return false;
    }
  }

  public String getResolvedPath() {
    if (StringUtils.isBlank(resolvedPath)) {
      resolvedPath = FastPathResolver.resolve(relativePath);
    }
    return resolvedPath;
  }

  public File asFile() throws IOException {
    if (file == null) {
      final String path = getResolvedPath();
      if (path == null) {
        throw new IOException(
            "Path " + relativePath + " could not be resolved relative to ");
      }

      file = new File(path);
      try {
        file = file.getCanonicalFile();
      } catch (SecurityException e) {
        throw new IOException(e);
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

package jp.spring.ioc.scan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import jp.spring.ioc.scan.beans.ClassData;
import jp.spring.ioc.scan.utils.FastPathResolver;
import jp.spring.ioc.scan.utils.ScanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 11/5/2017.
 */
public class ClasspathElementZip implements ClassPathElement {

  private Logger logger = LoggerFactory.getLogger(ClasspathElementZip.class);

  private ScanConfig scanSpecification;
  private ClassRelativePath path;
  private JarFile jar;
  private Map<String, ZipEntry> zips = new HashMap<>();
  private Map<String, ZipEntry> properties = new HashMap<>();

  ClasspathElementZip(ClassRelativePath classRelativePath, ScanConfig spec) throws IOException {
    this.scanSpecification = spec;
    this.path = classRelativePath;
  }

  private void scanZipFile(JarFile jar) {
    String prevPath = null;
    boolean prevMatch = false;
    for (Enumeration<? extends ZipEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
      final ZipEntry zipEntry = entries.nextElement();
      if (zipEntry.isDirectory()) {
        continue;
      } // Ignore directory entries, they are not used

      String relativePath = zipEntry.getName();
      final int lastSlashIdx = relativePath.lastIndexOf("/");
      final String parentRelativePath =
          lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
      final boolean pathChange = !parentRelativePath.equals(prevPath);
      final boolean matchStatus =
          (prevPath == null || pathChange)
              ? scanSpecification.isWhiteList(parentRelativePath)
              : prevMatch;
      prevPath = parentRelativePath;
      prevMatch = matchStatus;

      if (matchStatus) {
        if (ScanUtils.isClassFile(relativePath)) {
          zips.put(relativePath, zipEntry);
          logger.debug("found {}", relativePath);
        } else if (ScanUtils.isPropertiesFile(relativePath)) {
          properties.put(relativePath, zipEntry);
          logger.debug("found properties {}", relativePath);
        }
      }
    }
  }


  public ReadResult scan(ClassFileBinaryParser parser) {
    ReadResult res = new ReadResult();

    try {
      logger.debug("scan {}", path);
      scanZipFile(jar);
    } catch (Exception e) {
      logger.debug("scanning {} error, msg {}", path, e.getMessage());
    }

    // class
    List<ClassData> builders = new ArrayList<>();
    for (ZipEntry ze : zips.values()) {
      try {
        InputStream stream = jar.getInputStream(ze);
        ClassData b = parser.parse(stream);
        if (b != null) {
          builders.add(b);
        } else {
          logger.error("can't not read {}", ze);
        }
      } catch (Exception e) {
        logger.error("read {} error", ze);
      }
    }
    res.setBuilders(builders);

    // properties
    Properties pro = new Properties();
    for (ZipEntry p : this.properties.values()) {
      InputStream stream = null;
      try {
        stream = new BufferedInputStream(jar.getInputStream(p));
        pro.load(stream);
        stream.close();
      } catch (Exception e) {
        logger.error("read {} error", p);
      } finally {
        if (stream != null) {
          try {
            stream.close();
          } catch (IOException e) {
            logger.error("closing properties {} error:{}", p, e);
          }
        }
      }
    }
    res.setProperties(pro);
    return res;
  }

  public void close() {
    try {
      if (jar != null) {
        jar.close();
      }
      zips.clear();
      properties.clear();
    } catch (Exception e) {
      logger.error("close {}, error:{}", jar, e);
    }
  }

  @Override
  public void open(Deque<ClassRelativePath> queue) {
    try {
      jar = new JarFile(path.getFile());

      String classPath = jar.getManifest().getMainAttributes().getValue("Class-Path");
      if (classPath != null) {
        File f = path.getFile();
        String[] blocks = classPath.split(" ");
        logger.info("found Class-Path in {}", path);
        String parentPath = f.getCanonicalPath();
        int lastIdxOf = parentPath.lastIndexOf(File.separator);
        parentPath = 0 <= lastIdxOf ? parentPath.substring(0, lastIdxOf)
            : parentPath;
        logger.info("parent-path {}", parentPath);
        for (String s : blocks) {
          String path = FastPathResolver.normalizePath(parentPath + "/" + s);
          try {
            ClassRelativePath relativePath = new ClassRelativePath(path);
            queue.addLast(relativePath);
            logger.info("new element {}", s);
          } catch (Exception e) {
            logger.error("error on create relative path of {} msg-{}", path, e.getMessage());
          }
        }
      }
    } catch (IOException e) {
      logger.error("open manifest of" + path + " error", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClasspathElementZip that = (ClasspathElementZip) o;
    return Objects.equals(path, that.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path);
  }
}

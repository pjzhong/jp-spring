package jp.spring.ioc.scan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import jp.spring.ioc.scan.beans.ClassData;
import jp.spring.ioc.scan.utils.ScanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FastClassPathScanner的ClassElement设计不好，竟然在父类里面提供初始化子类的方法 虽然说不对外公开，但这样读起来真的很容疑惑。 .
 */
class ClassPathElementDir implements ClassPathElement {

  private static Logger logger = LoggerFactory.getLogger(ClassPathElementDir.class);
  private final ClassRelativePath path;
  private Map<String, File> files = new HashMap<>();//relativePath , File
  private Map<String, File> properties = new HashMap<>();
  private ScanConfig scanSpecification;

  ClassPathElementDir(ClassRelativePath classRelativePath, ScanConfig spec) {
    this.scanSpecification = spec;
    this.path = classRelativePath;
  }

  private void scanDir(File dir, int ignorePrefixLen,
      final Set<String> scanned) {
    String canonicalPath;
    try {
      canonicalPath = dir.getCanonicalPath();
      if (!scanned.add(canonicalPath)) {
        logger.info("Stop recursion at : {}", canonicalPath);
        return;
      }
    } catch (Exception e) {
      logger.error("scan {}, error:{}", dir, e);
      return;
    }

    final String dirPath = dir.getPath();
    final String dirRelatePath = dirPath.length() < ignorePrefixLen ?
        "/" : dirPath.substring(ignorePrefixLen).replace(File.separatorChar, '/');
    final boolean match = scanSpecification.isWhiteList(dirRelatePath);
    if (!match) {
      return;
    }

    final File[] files = dir.listFiles();
    if (files == null) {
      return;
    }

    for (final File file : files) {
      if (file.isDirectory()) {
        scanDir(file, ignorePrefixLen, scanned);
      } else if (file.isFile()) {
        String relativePath = dirRelatePath + file.getName();
        if (ScanUtils.isClassFile(relativePath)) {
          this.files.put(relativePath, file);
          logger.debug("found {}", relativePath);
        } else if (ScanUtils.isPropertiesFile(relativePath)) {
          this.properties.put(relativePath, file);
          logger.debug("found properties {}", relativePath);
        }
      }
    }
  }

  @Override
  public ReadResult scan(ClassFileBinaryParser parser) {
    ReadResult result = new ReadResult();

    File dir = path.getFile();
    logger.info("scan {}", dir.getPath());
    scanDir(dir, (dir.getPath().length() + 1), new HashSet<>());

    //Scan file
    List<ClassData> builders = new ArrayList<>();
    for (File file : files.values()) {
      try {
        InputStream stream = new FileInputStream(file);

        ClassData b = parser.parse(stream);
        if (b != null) {
          builders.add(b);
        } else {
          logger.error("can't not read {}", file);
        }
      } catch (IOException e) {
        logger.error("read {} error", file);
      }
    }
    result.setBuilders(builders);

    //read properties
    Properties pro = new Properties();
    for (File p : properties.values()) {
      InputStream stream = null;
      try {
        stream = new BufferedInputStream(new FileInputStream(p));
        pro.load(stream);
      } catch (Exception e) {
        logger.error("load properties {} error:{}", p, e);
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
    result.setProperties(pro);
    return result;
  }


  //do nothing;
  @Override
  public void close() {
    files.clear();
    properties.clear();
  }

  @Override
  public void open(Deque<ClassRelativePath> elements) {

  }
}

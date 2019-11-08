package jp.spring.ioc.scan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jp.spring.ioc.scan.beans.ClassData;
import jp.spring.ioc.scan.utils.ScanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 11/5/2017.
 */
public class ClasspathElementZip implements ClasspathElement {

  private Logger logger = LoggerFactory.getLogger(ClasspathElementZip.class);

  private ScanConfig scanSpecification;
  private ZipFile zipFile;
  private Map<String, ZipEntry> zips = new HashMap<>();
  private Map<String, ZipEntry> properties = new HashMap<>();

  ClasspathElementZip(ClassRelativePath classRelativePath, ScanConfig spec) {
    this.scanSpecification = spec;
    final File classFile = classRelativePath.asFile();
    if (classFile == null || !classFile.canRead()) {
      return;
    }

    try {
      zipFile = new ZipFile(classFile);
      logger.info("scan {}", zipFile.getName());
      scanZipFile(zipFile);
    } catch (IOException e) {
      logger.error("scanning {} error", classRelativePath);
    }
  }

  private void scanZipFile(ZipFile zipFile) {
    String prevPath = null;
    boolean prevMatch = false;
    for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
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


  public ReadResult read(ClassFileBinaryParser parser) {
    ReadResult res = new ReadResult();

    // class
    List<ClassData> builders = new ArrayList<>();
    for (ZipEntry ze : zips.values()) {
      try {
        InputStream stream = zipFile.getInputStream(ze);
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
        stream = new BufferedInputStream(zipFile.getInputStream(p));
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
      if (zipFile != null) {
        zipFile.close();
      }
      zips.clear();
      properties.clear();
    } catch (Exception e) {
      logger.error("close {}, error:{}", zipFile, e);
    }
  }

}

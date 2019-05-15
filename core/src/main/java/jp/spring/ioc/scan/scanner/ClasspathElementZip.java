package jp.spring.ioc.scan.scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 11/5/2017.
 */
public class ClasspathElementZip extends ClasspathElement<ZipEntry> {

  private ScanSpecification scanSpecification;
  private static Logger logger = LoggerFactory.getLogger(ClasspathElementZip.class);

  public ClasspathElementZip(ClassRelativePath classRelativePath, ScanSpecification spec) {
    super(classRelativePath);
    this.scanSpecification = spec;
    final File classpathFile;
    try {
      classpathFile = classRelativePath.asFile();
    } catch (IOException e) {
      ioExceptionOnOpen = true;
      return;
    }

    if (classpathFile == null || !classpathFile.canRead()) {
      ioExceptionOnOpen = true;
      return;
    }

    try {
      zipFile = new ZipFile(classpathFile);
      classFilesMap = new HashMap<>();
      scanZipFile(zipFile);
    } catch (IOException e) {
      ioExceptionOnOpen = true;
    }
  }

  private void scanZipFile(ZipFile zipFile) {
    String prevParentRelativePath = null;
    boolean prevMatchStatus = false;
    for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
      final ZipEntry zipEntry = entries.nextElement();
      if (zipEntry.isDirectory()) {
        continue;
      } // Ignore directory entries, they are not used

      String relativePath = zipEntry.getName();
      final int lastSlashIdx = relativePath.lastIndexOf("/");
      final String parentRelativePath =
          lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
      final boolean prevParentPathChange = !parentRelativePath.equals(prevParentRelativePath);
      final boolean matchStatus =
          (prevParentRelativePath == null || prevParentPathChange)
              ? scanSpecification.pathWhiteListMatchStatus(parentRelativePath)
              : prevMatchStatus;
      prevParentRelativePath = parentRelativePath;
      prevMatchStatus = matchStatus;

      if (matchStatus && ClassRelativePath.isClassFile(relativePath)) {
        classFilesMap.put(relativePath, zipEntry);
      }
    }
  }

  public List<ClassInfoBuilder> parse(ClassFileBinaryParser parser) {
    Collection<ZipEntry> files = classFilesMap.values();
    List<ClassInfoBuilder> builders = new ArrayList<>(files.size());

    for (ZipEntry file : files) {
      try {
        InputStream stream = zipFile.getInputStream(file);
        ClassInfoBuilder b = parser.parse(stream);
        if (b != null) {
          builders.add(b);
        } else {
          logger.error("can't not parse {}", file);
        }
      } catch (IOException e) {
        logger.error("parse {} error", file);
      }
    }
    return builders;
  }

  public void close() {
    try {
      if (zipFile != null) {
        zipFile.close();
      }
      classFilesMap.clear();
    } catch (IOException e) {
      logger.error("close {}, error:{}", zipFile, e);
    }
  }

  private ZipFile zipFile;
}

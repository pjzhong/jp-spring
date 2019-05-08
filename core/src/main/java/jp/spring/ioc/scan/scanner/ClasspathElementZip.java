package jp.spring.ioc.scan.scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 11/5/2017.
 */
public class ClasspathElementZip extends ClasspathElement<ZipEntry> {

  public ClasspathElementZip(ClassRelativePath classRelativePath, ScanSpecification spec,
      InterruptionChecker checker) {
    super(classRelativePath, spec, checker);
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
      classFilesMap = new HashMap<>(zipFile.size());//in case zero length
      scanZipFile(classRelativePath, zipFile);
    } catch (IOException e) {
      ioExceptionOnOpen = true;
      return;
    }
  }

  private void scanZipFile(ClassRelativePath classRelativePath, ZipFile zipFile) {
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

  public void close() {
    try {
      if (zipFile != null) {
        zipFile.close();
      }
      classFilesMap.clear();
    } catch (IOException e) {
      //todo log this
      throw new RuntimeException(e);
    }
  }

  private ZipFile zipFile;

  @Override
  public Iterator<InputStream> iterator() {
    return new Iterator<InputStream>() {
      private Iterator<ZipEntry> zipEntryIterator = classFilesMap.values().iterator();

      @Override
      public boolean hasNext() {
        return zipEntryIterator.hasNext();
      }

      @Override
      public InputStream next() {
        try {
          return zipFile.getInputStream(zipEntryIterator.next());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }
}

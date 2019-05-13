package jp.spring.ioc.scan.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * FastClassPathScanner的ClassElement设计不好，竟然在父类里面提供初始化子类的方法 虽然说不对外公开，但这样读起来真的很容疑惑。 .
 */
public class ClassPathElementDir extends ClasspathElement<File> {

  private ScanSpecification scanSpecification;

  public ClassPathElementDir(ClassRelativePath classRelativePath, ScanSpecification spec) {
    super(classRelativePath);
    this.scanSpecification = spec;
    File dir;
    try {
      dir = classRelativePath.asFile();
    } catch (IOException e) {
      ioExceptionOnOpen = true;
      return;
    }

    classFilesMap = new HashMap<>();
    scanDir(dir, (dir.getPath().length() + 1), new HashSet<>());

  }

  private void scanDir(File dir, int ignorePrefixLen,
      final Set<String> scanned) {
    String canonicalPath;
    try {
      canonicalPath = dir.getCanonicalPath();
      if (!scanned.add(canonicalPath)) {
        System.out.format("Stop recursion at : %s", canonicalPath);
        return;
      }
    } catch (IOException | SecurityException e) {
      //todo log this
      return;
    }

    final String dirPath = dir.getPath();
    final String dirRelatePath = ignorePrefixLen > dirPath.length() ?
        "/" : dirPath.substring(ignorePrefixLen).replace(File.separatorChar, '/') + "/";
    final boolean matchStatus = scanSpecification.pathWhiteListMatchStatus(dirRelatePath);
    if (!matchStatus) {
      return;
    }

    final File[] filesInDir = dir.listFiles();
    if (filesInDir == null) {
      return;
    }

    for (final File file : filesInDir) {
      if (file.isDirectory()) {
        scanDir(file, ignorePrefixLen, scanned);
      } else if (file.isFile()) {
        String fileRelativePath = dirRelatePath + file.getName();
        if (ClassRelativePath.isClassFile(fileRelativePath)) {
          classFilesMap.put(fileRelativePath, file);
        }
      }
    }
  }

  @Override
  public Iterator<InputStream> iterator() {
    return new Iterator<InputStream>() {
      private Iterator<File> fileIterator = classFilesMap.values().iterator();

      @Override
      public boolean hasNext() {
        return fileIterator.hasNext();
      }

      @Override
      public InputStream next() {
        try {
          return new FileInputStream(fileIterator.next());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  //do nothing;
  @Override
  public void close() {
    classFilesMap.clear();
  }
}

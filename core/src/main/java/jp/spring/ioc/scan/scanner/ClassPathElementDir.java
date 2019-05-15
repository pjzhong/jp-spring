package jp.spring.ioc.scan.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jp.spring.ioc.scan.beans.ClassInfoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FastClassPathScanner的ClassElement设计不好，竟然在父类里面提供初始化子类的方法 虽然说不对外公开，但这样读起来真的很容疑惑。 .
 */
public class ClassPathElementDir extends ClasspathElement<File> {

  private static Logger logger = LoggerFactory.getLogger(ClassPathElementDir.class);

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
        logger.info("Stop recursion at : {}", canonicalPath);
        return;
      }
    } catch (IOException | SecurityException e) {
      logger.error("scan {}, error:{}", dir, e);
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

  public List<ClassInfoBuilder> parse(ClassFileBinaryParser parser) {
    Collection<File> files = classFilesMap.values();
    List<ClassInfoBuilder> builders = new ArrayList<>(files.size());

    for (File file : files) {
      try {
        InputStream stream = new FileInputStream(file);
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


  //do nothing;
  @Override
  public void close() {
    classFilesMap.clear();
  }
}

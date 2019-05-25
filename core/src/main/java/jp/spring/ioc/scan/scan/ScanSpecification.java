package jp.spring.ioc.scan.scan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jp.spring.ioc.scan.utils.FastPathResolver;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/10/31.
 */
public class ScanSpecification {

  private List<String> whiteListPathPrefixes = new ArrayList<>();
  private Set<String> jrePaths;

  public ScanSpecification(List<String> scanned) {
    final Set<String> uniqueWhiteListPathPrefixes = new HashSet<>();
    for (String specification : scanned) {
      String specPath = specification.replace('.', '/');
      if (!specPath.equals("/")) {
        specPath += "/";
      }// '/' mean scan all
      uniqueWhiteListPathPrefixes.add(specPath);
    }

    jrePaths = getJrePaths();
    //process whiteListed
    whiteListPathPrefixes.addAll(uniqueWhiteListPathPrefixes);
  }

  public boolean isWhiteList(final String relatePath) {
    for (final String whiteList : whiteListPathPrefixes) {
      if (relatePath.equals(whiteList) || relatePath.startsWith(whiteList) ||
          whiteList.startsWith(relatePath) || "/".equals(relatePath)) {
        return true;
      }
    }

    return false;
  }

  public boolean blockJdk(final String relativePath) {
    for (String jrePath : jrePaths) {
      if (relativePath.startsWith(jrePath)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Obtain Jre path for block purpose
   *
   * @since 2019年05月25日 17:24:24
   */
  private Set<String> getJrePaths() {
    Set<File> jrePathFile = new HashSet<>();
    String javaHome = System.getProperty("java.home");
    if (StringUtils.isNotBlank(javaHome)) {
      File javaHomeFile = new File(javaHome);
      jrePathFile.add(new File(javaHome, "lib"));
      jrePathFile.add(new File(javaHome, "ext"));
      if (javaHomeFile.getName().equals("jre")) {  // Handle jre/../lib/tools.jar
        final File parent = javaHomeFile.getParentFile();
        if (parent != null) {
          jrePathFile.add(new File(parent, "lib"));
        }
      }
    }
    String javaExtDirs = System.getProperty("java.ext.dirs");
    if (StringUtils.isNotBlank(javaExtDirs)) {
      for (String javaExtDir : javaExtDirs.split(File.pathSeparator)) {
        if (StringUtils.isNotBlank(javaExtDir)) {
          jrePathFile.add(new File(javaExtDir));
        }
      }
    }

    Set<String> jrePathStr = new HashSet<>();
    for (File file : jrePathFile) {
      if (file.canRead() && file.isDirectory()) {
        String path = file.getPath();
        if (!path.endsWith(File.separator)) {
          path += File.separator;
        }

        final String jrePath = FastPathResolver.resolve(path);
        if (StringUtils.isNotBlank(jrePath)) {
          jrePathStr.add(jrePath);
        }
        try {
          String canonicalPath = file.getCanonicalPath();
          if (!canonicalPath.endsWith(File.separator)) {
            canonicalPath += File.separator;
          }
          String jreCanonicalPath = FastPathResolver.resolve(canonicalPath);
          if (!jreCanonicalPath.equals(canonicalPath) && StringUtils.isNotBlank(jreCanonicalPath)) {
            jrePathStr.add(jreCanonicalPath);
          }
        } catch (IOException | SecurityException e) {
          //do nothing == ignore;
        }
      }
    }

    return jrePathStr;
  }

}

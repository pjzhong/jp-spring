package jp.spring.ioc.scan.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/11/2.
 */
public class FastPathResolver {

  private static final boolean WINDOWS = File.separatorChar == '\\';

  /**
   * Translate backslashes to forward slashes, optionally removing trailing separator.
   */
  private static void translateSeparator(String path, StringBuilder buf) {
    for (int i = 0, endIdx = path.length(); i < endIdx; i++) {
      final char c = path.charAt(i);
      if (c == '\\' || c == '/') {
        if (i < endIdx - 1) {
          char prevChar = buf.length() == 0 ? '\0' : buf.charAt(buf.length() - 1);
          if (prevChar != '/') {
            buf.append('/');
          }
        }
      } else {
        buf.append(c);
      }
    }
  }


  public static String normalizePath(String path) {
    try {
      path = URLDecoder.decode(path, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    StringBuilder builder = new StringBuilder();
    translateSeparator(path, builder);
    return builder.toString();
  }

  public static String resolve(final String resolveBasePath, final String relativePathStr) {
    if (StringUtils.isBlank(relativePathStr)) {
      return resolveBasePath;
    }

    //We don't fetch remote classpath entries, although they are theoretically valid if
    // using a URLClassLoader
    if (relativePathStr.startsWith("http:") || relativePathStr.startsWith("https:")) {
      return "";
    }

    int startIdx = 0;
    if (relativePathStr.startsWith("jar:", startIdx)) {
      startIdx += 4;
    }

    boolean isAbsolutePath = false;
    String prefix = "";
    if (relativePathStr.startsWith("file:", startIdx)) {
      startIdx += 5;
      if (WINDOWS) {
        if (relativePathStr.startsWith("\\\\\\\\", startIdx)
            || relativePathStr.startsWith("////", startIdx)) {
          ///Windows UNC URL
          startIdx += 4;
          prefix = "//";
          isAbsolutePath = true;
        } else if (relativePathStr.startsWith("\\\\", startIdx)) {
          startIdx += 2;
        }
      }
      if (relativePathStr.startsWith("//", startIdx)) {
        startIdx += 2;
      }
    } else if (WINDOWS && (relativePathStr.startsWith("//") || relativePathStr.startsWith("\\"))) {
      startIdx += 2;
      prefix = "//";
      isAbsolutePath = true;
    }

    //Handle Windows path starting with a drive designation as an absolute path
    if (WINDOWS) {
      if (relativePathStr.length() - startIdx > 2
          && Character.isLetter(relativePathStr.charAt(startIdx))
          && relativePathStr.charAt(startIdx + 1) == ':') {
        isAbsolutePath = true;
      } else if (relativePathStr.length() - startIdx > 3
          && (relativePathStr.charAt(startIdx) == '/' || relativePathStr.charAt(startIdx) == '\\')
          && Character.isLetter(relativePathStr.charAt(startIdx + 1))
      ) {
        isAbsolutePath = true;
        startIdx++;
      }
    }

    // Catch-all for paths starting with separator
    if (relativePathStr.length() - startIdx > 1
        && (relativePathStr.charAt(startIdx) == '/' || relativePathStr.charAt(startIdx) == '\\')) {
      isAbsolutePath = true;
    }

    String pathStr = normalizePath(
        startIdx == 0 ? relativePathStr : relativePathStr.substring(startIdx));
    if (!prefix.isEmpty()) {
      pathStr = prefix + pathStr;
    }

    if (resolveBasePath == null || isAbsolutePath) {
      return pathStr;
    } else {
      return resolveBasePath + "/" + pathStr;
    }
  }
}

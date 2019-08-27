package jp.spring.ioc.scan.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 2017/11/2.
 */
public class FastPathResolver {

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
}

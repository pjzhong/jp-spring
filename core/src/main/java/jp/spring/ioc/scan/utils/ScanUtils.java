package jp.spring.ioc.scan.utils;

/**
 * Created by Administrator on 10/15/2017.
 */
public class ScanUtils {

  private static final String PROPERTIES = ".properties";

  public static boolean isJar(final String path) {
    final int len = path.length();
    return path.regionMatches(true, len - 4, ".jar", 0, 4) //
        || path.regionMatches(true, len - 4, ".zip", 0, 4) //
        || path.regionMatches(true, len - 4, ".war", 0, 4) //
        || path.regionMatches(true, len - 4, ".car", 0, 4) //
        || path.regionMatches(true, len - 6, ".wsjar", 0, 6);
  }

  /**
   * Returns true if path has a .class extension, ignoring case.
   */
  public static boolean isClassFile(final String path) {
    final int len = path.length();
    return len > 6 && path.regionMatches(true, len - 6, ".class", 0, 6);
  }

  /**
   * Returns true if path has a .properties extension, ignoring case.
   */
  public static boolean isPropertiesFile(final String path) {
    final int len = path.length();

    return len > PROPERTIES.length() && path
        .regionMatches(true, len - PROPERTIES.length(), PROPERTIES, 0, PROPERTIES.length());
  }
}

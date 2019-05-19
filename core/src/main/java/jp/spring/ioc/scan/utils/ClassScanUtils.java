package jp.spring.ioc.scan.utils;

/**
 * Created by Administrator on 10/15/2017.
 */
public class ClassScanUtils {

    public static boolean isJar(final String path) {
        final int len = path.length();
        return path.regionMatches(true, len - 4, ".jar", 0, 4) //
                || path.regionMatches(true, len - 4, ".zip", 0, 4) //
                || path.regionMatches(true, len - 4, ".war", 0, 4) //
                || path.regionMatches(true, len - 4, ".car", 0, 4) //
                || path.regionMatches(true, len - 6, ".wsjar", 0, 6);
    }
}

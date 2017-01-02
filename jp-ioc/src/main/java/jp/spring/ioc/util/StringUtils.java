package jp.spring.ioc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 12/15/2016.
 */
public class StringUtils {

    /**
     * Example: .... ${ANYTHING CAN BE HERE, BUT NOT starting with '}'} ......
     * */
    public static final Pattern PATTERN_CONFIG = Pattern.compile("(\\$\\{([^}]+)\\})");

    public static final Pattern PATTERN_PATH_VARIABLE = Pattern.compile("(\\{([^}]+)\\})");

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String decode(String s) {
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 首字母大写
     * */
    public static String upperFirst(String str) {
        if(StringUtils.isEmpty(str)) {
            return str;
        }

        char ch = str.charAt(0);
        if(ch < 'a' || ch > 'z') {
            return str;
        }

        char[] cs = str.toCharArray();
        cs[0] = Character.toUpperCase(ch);
        return String.valueOf(cs);
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String lowerFirst(String str) {
        if (StringUtils.isEmpty(str))
            return str;

        char ch = str.charAt(0);
        if (ch < 'A' || ch > 'Z')
            return str;

        char[] cs = str.toCharArray();
        cs[0] = Character.toLowerCase(ch);
        return String.valueOf(cs);
    }
}

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

    public static final Pattern TO_UNDERLINE = Pattern.compile("[A-Z]");

    public static final Pattern TO_CAML_HUMB = Pattern.compile("_[a-z]");

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmpty(String[] strs) {
        return strs == null || strs.length == 0;
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

    // 将驼峰风格替换为下划线风格
    public static String toUnderline(String str) {
        Matcher matcher = TO_UNDERLINE.matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    // 将下划线风格替换为驼峰风格
    public static String toCamelhump(String str) {
        Matcher matcher = TO_CAML_HUMB.matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() - i, matcher.end() - i, matcher.group().substring(1).toUpperCase());
        }
        if (Character.isUpperCase(builder.charAt(0))) {
            builder.replace(0, 1, String.valueOf(Character.toLowerCase(builder.charAt(0))));
        }
        return builder.toString();
    }
}

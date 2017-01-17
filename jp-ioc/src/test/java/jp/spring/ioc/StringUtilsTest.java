package jp.spring.ioc;

import jp.spring.ioc.util.StringUtils;
import org.junit.Test;

/**
 * Created by Administrator on 1/16/2017.
 */
public class StringUtilsTest {

    @Test
    public void test() {
        String name = "toUpperCase";

        System.out.println(StringUtils.toUnderline(name));
    }
}

package jp.spring.orm.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2/9/2017.
 */
public class JavaType {

    private Map<String, Class<?>> strToType = new HashMap<String, Class<?>>();

    public JavaType() {
       // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        strToType.put("java.lang.String", java.lang.String.class);

        // int, integer, tinyint, smallint, mediumint
        strToType.put("java.lang.Integer", java.lang.Integer.class);

        // bigint
        strToType.put("java.lang.Long", java.lang.Long.class);

        // date, year
        strToType.put("java.sql.Date", java.sql.Date.class);

        // real, double
        strToType.put("java.lang.Double", java.lang.Double.class);

        // float
        strToType.put("java.lang.Float", java.lang.Float.class);

        // bit
        strToType.put("java.lang.Boolean", java.lang.Boolean.class);

        // time
        strToType.put("java.sql.Time", java.sql.Time.class);

        // timestamp, datetime
        strToType.put("java.sql.Timestamp", java.sql.Timestamp.class);

        // decimal, numeric
        strToType.put("java.math.BigDecimal", java.math.BigDecimal.class);

        // unsigned bigint
        strToType.put("java.math.BigInteger", java.math.BigInteger.class);
    }

    public Class<?> getType(String typString) {
        return strToType.get(typString);
    }
}

package jp.spring.orm.helper;


import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.orm.annotation.Table;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 1/17/2017.
 * 方便生成一些简单的SQL;
 * 这里有一个规范，就是数据库字段 xxxx_xxx 都以这样的格式命名
 */
public class SQLHelper {

    private final static Pattern PLACE_HOLDER = Pattern.compile("\\?");

    public static String getSql(String key) {
        return ConfigHelper.getStringProperty(key);
    }

    public static String generateSelectSql(Class<?> cls, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("select ");

        String columnNames = getColumnName(cls);
        if(!StringUtils.isEmpty(columnNames)) {
            sql.append(columnNames);
        }

        String tableName = getTableName(cls);
        if(!StringUtils.isEmpty(tableName)) {
            sql.append(" from ").append(tableName).append(" ");
        }

        if(!StringUtils.isEmpty(condition)) {
            if(!JpUtils.isEmpty(params)) {
                condition = getWhereCondition(condition, params);
            }
            sql.append("where ").append(condition);
        }

        return sql.toString();
    }

    public static String generateInsertSQL(Class<?> cls, Map<String, Object> fieldMap) {
        StringBuilder sql = new StringBuilder("insert into ");

        String tableName = getTableName(cls);
        if(!StringUtils.isEmpty(tableName)) {
            sql.append(tableName).append(" ");
        }

        if(JpUtils.isEmpty(fieldMap)) {
            int i = 0;
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder(" values ");
            for(Map.Entry<String, ?> fieldEntry : fieldMap.entrySet()) {
                String columnName = StringUtils.toUnderline(fieldEntry.getKey());
                Object columnValue = fieldEntry.getValue();
                if(i == 0) {
                    columns.append("(").append(columnName);
                    values.append("('").append(columnValue).append("'");
                } else if(i == fieldMap.size() - 1) {
                    columns.append(", ").append(columnName).append(")");
                    values.append(", '").append(columnValue).append("')");
                } else {
                    columns.append(", ").append(columnName);
                    values.append(", '").append(columnValue).append("'");
                }
                i++;
            }
            sql.append(columns).append(values);
        }
        return sql.toString();
    }

    public static String generateDeleteSQL(Class<?> cls, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("delete from ");

        String tableName = getTableName(cls);
        if(!StringUtils.isEmpty(tableName)) {
            sql.append(tableName).append(" ");
        }

        if(!StringUtils.isEmpty(condition)) {
            if(!JpUtils.isEmpty(params)) {
                condition = getWhereCondition(condition, params);
            }
            sql.append(" where ").append(condition);
        }

        return sql.toString();
    }

    public static String generateUpdateSQL(Class<?> cls, Map<String, Object> fieldMap, String condition, Object... params) {
        StringBuilder sql = new StringBuilder("update ");

        String tableName = getTableName(cls);
        if(!StringUtils.isEmpty(tableName)) {
            sql.append(tableName).append(" ");
        }

        if(!JpUtils.isEmpty(fieldMap)) {
            sql.append("set ");
            int i = 0;
            for (Map.Entry<String, ?> fieldEntry : fieldMap.entrySet()) {
                String columnName = StringUtils.toUnderline(fieldEntry.getKey());
                Object columnValue = fieldEntry.getValue();
                if( i == 0) {
                    sql.append(columnName).append(" = '").append(columnValue).append("'");
                } else {
                    sql.append(", ").append(columnName).append(" = '").append(columnValue);
                }
                i++;
            }
        }

        if (!StringUtils.isEmpty(condition)) {
            if(!JpUtils.isEmpty(params)) {
                condition = getWhereCondition(condition, params);
            }
            sql.append(" where ").append(condition);
        }

        return sql.toString();
    }

    private static String getTableName(Class<?> cls) {
        String tableName;
        if(cls.isAnnotationPresent(Table.class)) {
            tableName = cls.getAnnotation(Table.class).value();
        } else {
            tableName = cls.getSimpleName().toLowerCase();
        }
        return tableName;
    }

    private static String getColumnName(Class<?> cls) {
        Map<String, String> columnNames = EntityHelper.getFieldMap(cls);
        StringBuilder builder = new StringBuilder();


        Iterator<Map.Entry<String, String>> values = columnNames.entrySet().iterator();
        while (values.hasNext()) {
            builder.append(values.next().getValue());

            if(values.hasNext()) {
                builder.append(", ");
            }
        }

        return builder.toString();
    }

    private static String getWhereCondition(String condition, Object... params) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PLACE_HOLDER.matcher(condition);
        for(int i = 0 ; matcher.find(); i++) {
            matcher.appendReplacement(buffer, "'" + params[i].toString() + "'");
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}

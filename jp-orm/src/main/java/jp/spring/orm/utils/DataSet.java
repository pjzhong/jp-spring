package jp.spring.orm.utils;

import jp.spring.orm.helper.DBHelper;
import jp.spring.orm.helper.SQLHelper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/18/2017.
 */
public class DataSet {

    public static <T> T select(Class<T> cls, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSql(cls, condition, params);
        return DBHelper.queryBean(cls, sql);
    }

    public static <T> List<T> selectList(Class<T> cls, String condition, Object... params) {
        String sql = SQLHelper.generateSelectSql(cls, condition, params);
        System.out.println(sql);
        return DBHelper.queryBeanList(cls, sql, params);
    }

    public static boolean insert(Class<?> cls , Map<String, Object> fieldMap) {
        String sql = SQLHelper.generateInsertSQL(cls, fieldMap);
        int rows = DBHelper.update(sql);
        return rows > 0;
    }

    public static boolean update(Class<?> cls, Map<String, Object> fieldMap, String condition, Object... params) {
        String sql = SQLHelper.generateUpdateSQL(cls, fieldMap, condition, params);
        int rows = DBHelper.update(sql);
        return rows > 0;
    }

    public static boolean delete(Class<?> cls, String condition, Object... params) {
        String sql = SQLHelper.generateDeleteSQL(cls, condition, params);
        int rows = DBHelper.update(sql);
        return rows > 0;
    }
}

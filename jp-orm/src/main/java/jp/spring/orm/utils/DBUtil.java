package jp.spring.orm.utils;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/16/2017.
 */
public class DBUtil {

    public static Object[] queryArray(QueryRunner runner, String sql, Object... params) {
        Object[] result = null;
        try {
            result = runner.query(sql, new ArrayHandler(), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Object[]> queryArrayList(QueryRunner runner, String sql, Object... params) {
        List<Object[]> result = null;
        try {
            result = runner.query(sql, new ArrayListHandler(), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    // 查询（返回 Map）
    public static Map<String, Object> queryMap(QueryRunner runner, String sql, Object... params) {
        Map<String, Object> result = null;
        try {
            result = runner.query(sql, new MapHandler(), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<Map<String, Object>> queryMapList(QueryRunner runner, String sql, Object... params) {
        List<Map<String, Object>> result = null;
        try {
            result = runner.query(sql, new MapListHandler(), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 查询（返回 Bean）
    public static <T> T queryBean(QueryRunner runner, Class<T> cls, Map<String, String> map, String sql, Object... params) {
        T result = null;
        try {
            if (map != null && !map.isEmpty()) {
                result = runner.query(sql, new BeanHandler<T>(cls, new BasicRowProcessor(new BeanProcessor(map))), params);
            } else {
                result = runner.query(sql, new BeanHandler<T>(cls), params);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> List<T> queryBeanList(QueryRunner runner, Class<T> cls, Map<String, String> map, String sql, Object... params) {
        List<T> result = null;
        try {
            if (map != null && !map.isEmpty()) {
                result = runner.query(sql, new BeanListHandler<T>(cls, new BasicRowProcessor(new BeanProcessor(map))), params);
            } else {
                result = runner.query(sql, new BeanListHandler<T>(cls), params);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 查询指定列名的值（多条数据）
    public static <T> List<T> queryColumnList(QueryRunner runner, String column, String sql, Object... params) {
        List<T> result = null;
        try {
            result = runner.query(sql, new ColumnListHandler<T>(column), params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 更新（包括 UPDATE、INSERT、DELETE，返回受影响的行数）
    public static int update(QueryRunner runner, Connection connection, String sql, Object... params) {
        int result = 0;
        try {
            if(connection != null) {
                result = runner.update(connection, sql, params);
            } else {
                result = runner.update(sql, params);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}

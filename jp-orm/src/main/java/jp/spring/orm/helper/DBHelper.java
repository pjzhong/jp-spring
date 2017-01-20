package jp.spring.orm.helper;

import jp.spring.orm.utils.DBUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 1/16/2017.
 */
public class DBHelper {
    private static final BasicDataSource dataSource = new BasicDataSource();
    private static final QueryRunner runner = new QueryRunner(dataSource);

    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>();

    static {
        dataSource.setDriverClassName(ConfigHelper.getStringProperty("jdbc.driver"));
        dataSource.setUrl(ConfigHelper.getStringProperty("jdbc.url"));
        dataSource.setUsername(ConfigHelper.getStringProperty("jdbc.username"));
        dataSource.setPassword(ConfigHelper.getStringProperty("jdbc.password"));
        dataSource.setMaxActive(ConfigHelper.getNUmberProperty("jdbc.max.active"));
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void beginTransaction() {
        Connection connection = connectionThreadLocal.get();
        if(connection == null) {
            try {
                connection = getDataSource().getConnection();
                connection.setAutoCommit(false);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectionThreadLocal.set(connection);
            }
        }
    }

    public static void commitTransaction() {
        Connection connection = connectionThreadLocal.get();
        if(connection != null) {
            try {
                connection.commit();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectionThreadLocal.remove();
            }
        }
    }

    public static void rollbackTransaction() {
        Connection connection = connectionThreadLocal.get();
        if(connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connectionThreadLocal.remove();
            }
        }
    }

    public static <T>  T queryBean(Class<T> cls, String sql, Object... params) {
        Map<String, String> map = EntityHelper.getFieldMap(cls);
        return DBUtil.queryBean(runner, cls, map, sql, params);
    }

    public static <T> List<T> queryBeanList(Class<T> cls, String sql, Object... params) {
        Map<String, String> map = EntityHelper.getFieldMap(cls);
        return DBUtil.queryBeanList(runner, cls, map, sql, params);
    }

    public static int update(String sql, Object... params) {
        Connection connection = connectionThreadLocal.get();
        return DBUtil.update(runner, connection, sql, params);
    }
}

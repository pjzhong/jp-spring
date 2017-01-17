package jp.spring.orm.helper;

import jp.spring.orm.utils.DBUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 1/16/2017.
 */
public class DBHelper {
    private static final BasicDataSource dataSource = new BasicDataSource();
    private static final QueryRunner runner = new QueryRunner(dataSource);

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

    public static <T>  T queryBean(Class<T> cls, String sql, Object... params) {
        Map<String, String> map = EntityHelper.getFieldMap(cls);
        return DBUtil.queryBean(runner, cls, map, sql, params);
    }

    public static <T> List<T> queryBeanList(Class<T> cls, String sql, Object... params) {
        Map<String, String> map = EntityHelper.getFieldMap(cls);
        return DBUtil.queryBeanList(runner, cls, map, sql, params);
    }

    public static int update(String sql, Object... params) {
        return DBUtil.update(runner, sql, params);
    }
}

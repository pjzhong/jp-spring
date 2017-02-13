package jp.spring.orm;

import jp.spring.orm.builder.TableBuilder;
import jp.spring.orm.dialect.Dialect;
import jp.spring.orm.dialect.MySqlDialect;
import jp.spring.orm.pool.DataSourceProvider;
import jp.spring.orm.pool.impl.DefaultDataSourceProvider;
import jp.spring.orm.utils.DbUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2/7/2017.
 */
public class ActiveRecord {
    private final static Logger logger = LogManager.getLogger(ActiveRecord.class);

    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<Connection>();

    /**
     * Single model
     * */
    private static ActiveRecord instance;

    /**
     * Provider以后会变成Factory, 现在先这样保留着
     * */
    private DataSourceProvider dataSourceProvider = null;

    private DataSource dataSource;

    private Dialect dialect;

    /**
     * Config将会被移除，因为我还不熟悉JFinal为什么要这样设计
     * */
    private Config config = null;
    private final List<Table> tables = new ArrayList<Table>();

    private ActiveRecord() {}

    /**
     * ActiveRecord will be initialized in OrmBeanPostProcessor
     * */
    public static ActiveRecord init(String url, String driver, String user, String password) {
        if(instance == null) {
            DataSourceProvider provider = new DefaultDataSourceProvider(driver, url, user, password);
            instance = new ActiveRecord(provider);
            instance.dialect = new MySqlDialect();
        }

        return instance;
    }

    /**
     * Call this method after init()
     * */
    public static ActiveRecord getInstance() {
        return instance;
    }

    public static Dialect getDialect() {
        return  getInstance().dialect;
    }

    public static Connection getConnection() {
        Connection connection = threadLocalConnection.get();
        if(connection == null) {
            try {
                connection = getInstance().dataSource.getConnection();
                threadLocalConnection.set(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return connection;
    }

    public ActiveRecord(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
        config = new Config("test", dataSourceProvider.getDataSource());
        dataSource = dataSourceProvider.getDataSource();
    }

    public static void beginTransaction() {
        Connection conn = getConnection();
        if(conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                logger.error("Open transaction failed");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Commit transaction
     * */
    public static void commitTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                logger.error("提交事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                close(conn);
            }
        }
    }

    /**
     * rollback transaction
     */
    public static void rollbackTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("回滚事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                close(conn);
            }
        }
    }

    /* Utils Method */
    public final static void close(AutoCloseable... autoCloseables) {
        for(AutoCloseable close : autoCloseables) {
            try {
                if(close != null) {
                    if(close instanceof Connection) {
                        closeConnection((Connection) close);
                    } else {
                        close.close();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error raised when closing " + e, e);
            }
        }
    }

    private static void closeConnection(Connection connection) throws Exception {
        if(connection.getAutoCommit()) { // In a transaction if autocommit is false, let the proxy handle it
            threadLocalConnection.remove();
            connection.close();
        }
    }

    /**
     * 这个可以使用IOC的扫描来完成扫描来代替，不需要手工添加
     * */
    public ActiveRecord addMapping(String tableName, String primaryKey, Class<? extends Model<?>> modelClass) {
        tables.add(new Table(tableName, primaryKey, modelClass));
        return this;
    }

    /**
     * 这个模块初始化可以放到beanPostProcessor里面去做
     * */
    public boolean start() {
        if(dataSourceProvider != null) {
            config.setDataSource(dataSourceProvider.getDataSource());
        }
        if(config.getDataSource() == null ) {
            throw new RuntimeException("Need DataSource or DataSourceProvider");
        }

        new TableBuilder().build(tables);
        DbUtils.addConfig(config);
        return true;
    }
}

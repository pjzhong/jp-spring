package jp.spring.orm;

import jp.spring.ioc.util.StringUtils;
import jp.spring.orm.dialect.Dialect;
import jp.spring.orm.dialect.MySqlDialect;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Administrator on 2/7/2017.
 */
public class Config {

    private final static Logger logger = LogManager.getLogger(Config.class);

    private final ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    private String name;
    private DataSource dataSource;
    private Dialect dialect;
    private int transactionLevel;

    public Config(String name, DataSource dataSource) {
        this(name, dataSource, new MySqlDialect());
    }

    public Config(String name, DataSource dataSource, Dialect dialect) {
        this(name, dataSource, dialect, 0);
    }

    public Config(String name, DataSource dataSource, Dialect dialect, int transactionLevel) {
        init(name, dataSource, dialect, transactionLevel);
    }

    private void init(String name, DataSource dataSource, Dialect dialect, int transactionLevel) {
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Config name can not be null");
        }
        if(dialect == null) {
            throw new IllegalArgumentException("Dialect can not be null");
        }

        this.name = name.trim();
        this.dataSource = dataSource;
        this.dialect = dialect;
        this.setTransactionLevel(transactionLevel);
    }

    public void setTransactionLevel(int transactionLevel) {
        int t = transactionLevel;
        switch (t) {
            case Connection.TRANSACTION_NONE:
            case Connection.TRANSACTION_READ_UNCOMMITTED:
            case Connection.TRANSACTION_READ_COMMITTED:
            case Connection.TRANSACTION_REPEATABLE_READ:
            case Connection.TRANSACTION_SERIALIZABLE:break;
            default: throw new IllegalArgumentException("The transactionLevel only be 0, 1, 2, 4, 8");
        }
        this.transactionLevel = transactionLevel;
    }

    public final void setThreadLocalConnection(Connection connection) {
        threadLocal.set(connection);
    }

    public final void removeThreadLocalConnection() {
        threadLocal.remove();
    }

    public final Connection getConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if(connection == null) {
             connection = dataSource.getConnection();
              threadLocal.set(connection);
        }

        return connection;
    }

    /*Getters and setters*/

    public String getName() {
        return name;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public int getTransactionLevel() {
        return transactionLevel;
    }
}

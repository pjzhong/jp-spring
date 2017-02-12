package jp.spring.orm.pool.impl;

import jp.spring.orm.pool.DataSourceProvider;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2/7/2017.
 */
public class DefaultDataSourceProvider implements DataSource , DataSourceProvider{

    private final String DriverClass;
    private final String url;
    private final String username;
    private final String password;

    /**
     * @param driverClass The name of the jdbc driver class provied by user
     * @param url The address of the database, provide by user
     * @param username The user of the database, provide by user
     * @param password The password of the database, provide by user
     * */
    public DefaultDataSourceProvider(String driverClass, String url, String username, String password) {
        this.DriverClass = driverClass;
        this.url = url;
        this.username = username;
        this.password = password;
        init();
    }

    private void init() {
        try {
            Class.forName(this.DriverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return a new connection each time this method called。
     */
    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(this.username, this.password);
    }

    /**
     * return a new connection each time this method called。
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(this.url, username, password);
    }

    /**
     * return a datasource
     * */
    @Override
    public DataSource getDataSource() {
        return this;
    }

    /*Unsupported methods*/
    /**
     * Unsupported, never call this method;
     * */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unsupported, never call this method;
     * */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unsupported, never call this method;
     * */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    /**
     * Unsupported, never call this method;
     * */
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unsupported, never call this method;
     * */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Unsupported, never call this method;
     * */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

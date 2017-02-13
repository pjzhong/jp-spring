package jp.spring.orm.pool;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2/7/2017.
 */
public interface DataSourceProvider {
    DataSource getDataSource();
}

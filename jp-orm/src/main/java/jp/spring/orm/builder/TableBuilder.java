package jp.spring.orm.builder;

import jp.spring.orm.ActiveRecord;
import jp.spring.orm.dialect.Dialect;
import jp.spring.orm.Table;
import jp.spring.orm.support.TableMapping;
import jp.spring.orm.support.JavaType;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2/9/2017.
 */
public class TableBuilder {

    private JavaType javaType = new JavaType();

    public void build(List<Table> tableList) {
        Table table = null;
        Connection conn = null;

        try {
            conn = ActiveRecord.getConnection();
            TableMapping tableMapping = TableMapping.getInstance();
            for(int i = 0; i < tableList.size(); i++) {
                table = tableList.get(i);
                doBuild(table, conn, ActiveRecord.getDialect());
                tableMapping.putTable(table);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Can not create Table object, maybe the table " + table.getName() + " is not exists",
                    e);
        } finally {
            ActiveRecord.close(conn);
        }
    }

    private void doBuild(Table table, Connection conn, Dialect dialect) throws SQLException {
        table.setColumnTypeMap(new HashMap<String, Class<?>>());

        String sql = dialect.forTableBuilderDoBuild(table.getName());
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        for(int i=1; i <= rsmd.getColumnCount(); i++) {
            String colName = rsmd.getColumnName(i);
            String colClassName = rsmd.getColumnClassName(i);

            Class<?> clazz = javaType.getType(colClassName);
            if(clazz != null) {
                table.setColumnType(colName, clazz);
            } else {
                int type = rsmd.getColumnType(i);
                switch (type) {
                    case Types.BINARY:
                    case Types.VARBINARY:
                    case Types.BLOB: table.setColumnType(colName, byte[].class);break;
                    default:table.setColumnType(colName, String.class);
                }
            }
        }

        ActiveRecord.close(rs, stm);
    }
}

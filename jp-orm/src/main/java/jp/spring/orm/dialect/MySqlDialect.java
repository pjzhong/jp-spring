package jp.spring.orm.dialect;

import jp.spring.orm.Table;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2/7/2017.
 * MYSQL CRUD generator
 */
public class MySqlDialect extends Dialect {
    @Override
    public String forTableBuilderDoBuild(String tableName) {
        return "SELECT * FROM `" + tableName + "` WHERE 1 = 2";
    }

    /**
     * @param pageNumber
     * @param pageSize
     * @param select select SQL without where condition(Not not yet)?
     * @param sqlExceptSelect where conditions or something else?
     * */
    @Override
    public String forPaginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
        int offset = pageSize * (pageNumber - 1);
        StringBuilder builder = new StringBuilder();
        builder.append(select).append(" ").append(sqlExceptSelect);
        builder.append(" limit ").append(offset).append(", ").append(pageSize);
        return builder.toString();
    }

    @Override
    public String forModelFindById(Table table, String[] columns) {
        StringBuilder sql = new StringBuilder("SELECT ");

        for(int i = 0; i < columns.length; i++) {
            if (i > 0) { sql.append(",");}
            sql.append("`").append(columns[i]).append("`");
        }

        sql.append(" FROM `").append(table.getName()).append("` WHERE ");
        String[] primaryKeys = table.getPrimaryKey();
        for(int i = 0; i < primaryKeys.length; i++) {
            if(i > 0) { sql.append(" AND "); }
            sql.append("`").append(primaryKeys[i]).append("` = ?");
        }
        return sql.toString();
    }

    @Override
    public String forModelDeleteById(Table table) {
        String[] primaryKeys = table.getPrimaryKey();
        StringBuilder sql = new StringBuilder();

        sql.append("DELETE FROM `").append(table.getName()).append("` WHERE ");
        for(int i = 0; i < primaryKeys.length; i++) {
            if(i > 0) { sql.append(" AND "); }
            sql.append("`").append(primaryKeys[i]).append("` = ?");
        }
        return sql.toString();
    }

    @Override
    public String forModelSave(Table table, Map<String, Object> attrs, List<Object> paras) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO `").append(table.getName()).append("` (");
        StringBuilder values = new StringBuilder(") VALUES(");

        String colName;
        for(Map.Entry<String, Object> entry : attrs.entrySet()) {
            colName  = entry.getKey();
            if (table.hasColumn(colName)) {
                if(paras.size() > 0) {
                    sql.append(", ");
                    values.append(", ");
                }
                sql.append("`").append(colName).append("`");
                values.append("?");
                paras.add(entry.getValue());
            }
        }
        sql.append(values.toString()).append(")");

        return sql.toString();
    }

    @Override
    public String forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, List<Object> paras) {
        StringBuilder sql = new StringBuilder();

        sql.append("UPDATE `").append(table.getName()).append("` SET ");
        String[] primaryKeys = table.getPrimaryKey();
        for(Map.Entry<String, Object> entry : attrs.entrySet()) {// setting update data
            String colName = entry.getKey();
            if(modifyFlag.contains(colName) && !isPrimaryKey(colName, primaryKeys) && table.hasColumn(colName)) {
                if(paras.size() > 0) { sql.append(", "); }
                sql.append("`").append(colName).append(" = ?");
                paras.add(entry.getValue());
            }
        }
        sql.append(" WHERE ");
        for(int i = 0; i < primaryKeys.length; i++) { //setting update primary key;
            if( i > 0 ) { sql.append(" AND "); }
            sql.append("`").append(primaryKeys[i]).append("` = ?");
            paras.add(attrs.get(primaryKeys[i]));
        }

        return sql.toString();
    }
}

package jp.spring.orm.dialect;

import jp.spring.ioc.util.JpUtils;
import jp.spring.orm.Table;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2/7/2017.
 */
public abstract  class Dialect {

    //Method for common
    public abstract String forTableBuilderDoBuild(String tableName);
    public abstract String forPaginate(int pageNumber, int pageSize, String select, String sqlExceptSelect);

    //Method for Model
    public abstract String forModelFindById(Table table, String[] columns);
    public abstract String forModelDeleteById(Table table);
    public abstract String forModelSave(Table table, Map<String, Object> attrs, List<Object> paras);
    public abstract String forModelUpdate(Table table, Map<String, Object> attrs, Set<String> modifyFlag, List<Object> paras);


    //Utils Method
    public boolean isPrimaryKey(String colName, String[] primaryKeys) {
        for(String key : primaryKeys) {
            if(colName.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return  false;
    }

    public void trimPrimaryKeys(String[] primaryKeys) {
        for(int i = 0; i < primaryKeys.length; i++) {
            primaryKeys[i] = primaryKeys[i].trim();
        }
    }

    public void fillStatement(PreparedStatement pst, List<Object> paras) throws SQLException {
        if(!JpUtils.isEmpty(paras)) {
            for(int i = 0; i < paras.size(); i ++) {
                pst.setObject(i + 1, paras.get(i));
            }
        }
    }

    public void fillStatement(PreparedStatement pst, Object... paras) throws SQLException {
        if(!JpUtils.isEmpty(paras)) {
            for(int i = 0; i < paras.length; i ++) {
                pst.setObject(i + 1, paras[i]);
            }
        }
    }
}

package jp.spring.orm;

import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.orm.Model;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2/7/2017.
 */
public class Table {

    private String name;
    private String[] primaryKey = null;
    private Class<? extends Model<?>> modelClass;
    private String[] columns = null;
    private Map<String, Class<?>> columnTypeMap;

    public Table(String name) {
        this.name = name;
    }

    /**
     * Construct a table object without Primary key
     * @param name table name
     * @param modelClass the model represent the table
     * */
    public Table(String name, Class<? extends Model<?>> modelClass) {
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Table name can not be null");
        }
        if(modelClass == null) {
            throw new IllegalArgumentException("Model class can not be null");
        }

        this.name = name.trim();
        this.modelClass = modelClass;
    }

    /**
     * Construct a table object without Primary key
     * @param name table name
     * @param  primaryKey the primaryKeys of this table(if is composite key, separate by ',')
     * @param modelClass the model represent the table
     * */
    public Table(String name, String primaryKey, Class<? extends Model<?>> modelClass) {
        this(name, modelClass);
        if(StringUtils.isEmpty(primaryKey)) {
            throw new IllegalArgumentException("Primary key can not be null");
        }
        this.primaryKey = primaryKey.split("\\s*,\\s*");
    }

    public void setColumnTypeMap(Map<String, Class<?>> columnTypeMap) {
        this.columnTypeMap = columnTypeMap;
    }

    public void setColumnType(String columnName, Class<?> columnType) {
        columnTypeMap.put(columnName, columnType);
    }

    public Class<?> getColumnType(String columnLabel) {
        return columnTypeMap.get(columnLabel);
    }

    public String[] getColumns() {
        if(JpUtils.isEmpty(columns)) {
            columns = new String[columnTypeMap.keySet().size()];
            columns  = columnTypeMap.keySet().toArray(columns);
        }
        return columns;
    }

    /**
     * Model.save() need know what columns belongs to himself that he can saving to db.
     * Think about auto saving the related table's column in the future.
     */
    public boolean hasColumn(String columnName) {
        return columnTypeMap.containsKey(columnName);
    }

    /**
     * update() and delete() need this method;
     * */
    public String[] getPrimaryKey() {
        return primaryKey;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Model<?>> getModelClass() {
        return modelClass;
    }
}

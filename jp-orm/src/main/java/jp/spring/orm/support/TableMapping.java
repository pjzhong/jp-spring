package jp.spring.orm.support;

import jp.spring.orm.Model;
import jp.spring.orm.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2/8/2017.
 */
public class TableMapping {

    private final Map<Class<? extends Model<?>>, Table> modelToTableMap = new HashMap<Class<? extends Model<?>>, Table>();

    private final static TableMapping instance = new TableMapping();// Single Model

    private TableMapping() {}

    public static TableMapping getInstance() {
        return instance;
    }

    public  void putTable(Table table) {
        modelToTableMap.put(table.getModelClass(), table);
    }

    public Table getTable(Class<? extends Model> modelClass) {
        return modelToTableMap.get(modelClass);
    }
}

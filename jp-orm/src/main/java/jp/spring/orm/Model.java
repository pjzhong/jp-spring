package jp.spring.orm;

import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.orm.annotation.Entity;
import jp.spring.orm.dialect.Dialect;
import jp.spring.orm.support.TableMapping;
import jp.spring.orm.support.ModelBuilder;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 * Created by Administrator on 2/7/2017.
 */
@Entity
public abstract class Model<M extends Model> implements Serializable {

    private static final long serialVersionUID = -2721279876965055075L;

    /**
     * like the field in POJO
     * in POJO:
     * class POJO {
     *     int example = 1;
     * }
     *
     * in model:
     * class model {
     *    Map<String, Object>attributes ={ {"example":1}};
     * }
     *
     * */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * Flag of column has been modified. update need this falg
     * */
    private Set<String> modifyFlag = new HashSet<String>();

    /**
     * get attribute from this model
     * @param attr the key or the name of the attribute
     * */
    public <T> T get(String attr) {
        return (T)(getAttributes().get(attr));
    }

    /**
     * Set attribute to model.
     * @param attr the attribute name of the model
     * @parm value the value of attribute
     * @return this Model
     * @throws IllegalAccessException if the attribute is not exists of the model
     * */
    public M set(String attr, Object value) {
        Table table = getTable();
        if(table.hasColumn(attr)) {
            getAttributes().put(attr, value);
            getModifyFlag().add(attr);
            return (M)this;
        }
        throw new IllegalArgumentException("The attribute name does not exists: " + attr);
    }

    /**
     * Remove attributes from this model
     * @param attributes the name of the attributes or the key to the attribute
     */
    public M remove(String... attributes) {
        if(!JpUtils.isEmpty(attributes)) {
            for(String a : attributes) {
                this.getAttributes().remove(a);
                this.getModifyFlag().remove(a);
            }
        }
        return (M) this;
    }

    /**
     * Clear all attributes from this model
     * */
    public M clear() {
        getAttributes().clear();
        getModifyFlag().clear();
        return (M)this;
    }

    /**
     * Save model
     * */
    public boolean save() {
        Table table = getTable();

        List<Object> paras = new ArrayList<Object>();
        String sql = ActiveRecord.getDialect().forModelSave(table, attributes, paras);

        Connection connection = null;
        PreparedStatement pst = null;
        int result = 0;
        try {
            connection =  ActiveRecord.getConnection();
            pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ActiveRecord.getDialect().fillStatement(pst, paras);
            result = pst.executeUpdate();
            getGeneratedKey(pst, table);
            getModifyFlag().clear();
            return  result >= 1;
        } catch (Exception e) {
            throw  new RuntimeException(e);
        } finally {
            ActiveRecord.close(pst, connection);
        }
    }

    private void getGeneratedKey(PreparedStatement pst, Table table) throws SQLException {
        String[] primaryKeys = table.getPrimaryKey();
        ResultSet rs= pst.getGeneratedKeys();
        for(String key : primaryKeys) {
            if(get(key) == null) {
                if(rs.next()) {
                    Class<?> colType = table.getColumnType(key);
                    if(colType == Integer.class || colType == int.class) {
                        set(key, rs.getInt(1));
                    } else if(colType == Long.class || colType == long.class) {
                        set(key, rs.getLong(1));
                    } else {
                        set(key, rs.getObject(1));
                    }
                }
            }
        }
        rs.close();
    }

    /**
     * Delete model.
     * */
    public boolean delete() {
        Table table = getTable();
        String[] primaryKeys = table.getPrimaryKey();
        Object[] ids = new Object[primaryKeys.length];
        for(int i = 0 ; i < primaryKeys.length; i++) {
            ids[i] = getAttributes().get(primaryKeys[i]);
            if(ids[i] == null) {
                throw new RuntimeException("You can't delete model without primary key value," + primaryKeys[i]);
            }
        }

        return doDeleteById(table, ids);
    }

    public boolean deleteById(Object idValue) {
        if(null == idValue) {
            throw new IllegalArgumentException("IdValue can't be null");
        }

        return doDeleteById(getTable(), idValue);
    }

    private boolean doDeleteById(Table table, Object... idValues) {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn =  ActiveRecord.getConnection();
            String sql = ActiveRecord.getDialect().forModelDeleteById(table);
            int result = doUpdateSql(ActiveRecord.getDialect(), conn, sql, idValues);
            return result > 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ActiveRecord.close(pst, conn);
        }
    }

    /**
     * Update model.
     * */
    public boolean update() {
        if(getModifyFlag().isEmpty()) {
            return false;
        }

        Table table = getTable();
        String[] primaryKeys = table.getPrimaryKey();
        for(String key : primaryKeys) {
            Object id = getAttributes().get(key);
            if(id == null) {
                throw new RuntimeException("You can't update model without primary key,"
                + key + "can't be null");
            }
        }

        List<Object> paras = new ArrayList<Object>();
        String sql = ActiveRecord.getDialect().forModelUpdate(table, getAttributes(),getModifyFlag(), paras);
        if(paras.size() <= 1) { return false; } // No need for update

        Connection conn = null;
        try {
            conn =  ActiveRecord.getConnection() ;
            int result = doUpdateSql(ActiveRecord.getDialect(), conn, sql, paras.toArray());
            if(result >= 1) {
                getModifyFlag().clear();
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ActiveRecord.close(conn);
        }
    }

    private int doUpdateSql(Dialect dialect, Connection conn, String sql, Object... paras) throws Exception {
        PreparedStatement pst = null;
        int result;
        try {
             pst = conn.prepareStatement(sql);
             dialect.fillStatement(pst, paras);
             result = pst.executeUpdate();
        } finally {
            ActiveRecord.close(pst);
        }
        return result;
    }

    /**
     * Find model by Id and load all columns only
     * <pre>
     * Example:
     * User user = User.dao.findByIdLoadColumns(123);
     * </pre>
     * @param ids the id value of the model
     * */
    public M findById(Object... ids) {
        return findById(null, ids);
    } 
    
    /**
     * Find model by Id and load specific columns only
     * <pre>
     * Example:
     * User user = User.dao.findByIdLoadColumns(123, "name, age");
     * </pre>
     * @param ids the id value of the model
     * @param columns the specific columns to load
     * */
    public M findById(String columns, Object... ids) {
        Table table = getTable();
        if(table.getPrimaryKey().length != ids.length) {
            throw new IllegalArgumentException("id values error, need " + table.getPrimaryKey().length + " id value");
        }

        String[] columnNames = StringUtils.isEmpty(columns) ? table.getColumns() : columns.split("\\s*,\\s*");
        String sql = ActiveRecord.getDialect().forModelFindById(table, columnNames);
        List<M> result = find(sql, ids);
        return result.size() > 0 ? result.get(0) : null;
    }

    /**
     * Find model from databases
     * */
    public List<M> find(String sql) {
        return find(sql, null);
    }

    public List<M> find(String sql, Object... paras) {
        Connection conn = null;
        try {
           conn = ActiveRecord.getConnection();
           return doFind(conn, sql, paras);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ActiveRecord.close(conn);
        }
    }

    private List<M> doFind(Connection conn, String sql, Object... paras) throws Exception {
        PreparedStatement pst = conn.prepareStatement(sql);
        ActiveRecord.getDialect().fillStatement(pst, paras);
        ResultSet rs = pst.executeQuery();

        Class<? extends Model> modelClass = getUsefulClass();
        List<M> result = ModelBuilder.build(rs, modelClass);
        ActiveRecord.close(rs, pst);
        return result;
    }

    /* Utils Method */
    private Table getTable() {
        return TableMapping.getInstance().getTable(getUsefulClass());
    }

    private Class<? extends Model> getUsefulClass() {
        Class c = getClass();
        return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass(); // com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
    }
    
    /* Getters ,setters,toString, equal, hash*/
    public Set<String> getModifyFlag() {
        return modifyFlag;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        boolean first = true;
        for(Map.Entry<String, Object> e : getAttributes().entrySet()) {
            if(first) { first = false;}
            else { builder.append(", "); }

            Object value = e.getValue() != null ? e.getValue().toString() : e.getValue();
            builder.append(e.getKey()).append(":").append(value);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof  Model) {
            Model that = (Model) o;
            if( (that == this)
                    || (this.getUsefulClass() == that.getUsefulClass())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = attributes != null ? attributes.hashCode() : 0;
        result = 31 * result + (modifyFlag != null ? modifyFlag.hashCode() : 0);
        return result;
    }
}

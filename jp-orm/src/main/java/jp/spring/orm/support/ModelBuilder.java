package jp.spring.orm.support;

import jp.spring.orm.ActiveRecord;
import jp.spring.orm.Model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2/10/2017.
 */
public class ModelBuilder {

    public static final <T> List<T> build(ResultSet rs, Class<? extends Model> modelClass) throws Exception {
        List<T> result = new ArrayList<T>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String[] labelNames = new String[columnCount + 1];
        int[] types = new int[columnCount + 1];
        buildLabelNamesAndTypes(rsmd, labelNames, types);

        while (rs.next()) {
            Model<?> model = modelClass.newInstance();
            Map<String, Object> attributes = model.getAttributes();

            Object value = null;
            for(int i = 1; i <=columnCount; i++) {
                switch (types[i]) {
                    case Types.CLOB: value = handleClob(rs.getClob(i));break;
                    case Types.NCLOB: value = handleClob(rs.getNClob(i));break;
                    case Types.BLOB: value = handleBlob(rs.getBlob(i));break;
                    default: value = rs.getObject(i);
                }

                attributes.put(labelNames[i], value);
            }
            result.add((T) model);
        }

        return result;
    }

    private static final void buildLabelNamesAndTypes(ResultSetMetaData rsmd, String[] labelNames, int[] types) throws Exception {
        for(int i = 1; i < labelNames.length; i++) {
            labelNames[i] = rsmd.getColumnLabel(i);
            types[i] = rsmd.getColumnType(i);
        }
    }

    public static  byte[] handleBlob(Blob blob) throws SQLException {
        if(null != blob) {
            InputStream is = null;
            try {
                is = blob.getBinaryStream();
                if( (is != null) && (blob.length() != 0) ) {
                    byte[] data = new byte[(int) blob.length()];
                    is.read(data);
                    return data;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                ActiveRecord.close(is);
            }
        }
        return null;
    }

    private static String handleClob(Clob clob) throws SQLException {
        if(null != clob) {
            Reader reader = clob.getCharacterStream();
            try {
                if( (reader != null) && (clob.length() != 0) ) {
                    char[] buffer= new char[(int)clob.length()];
                    reader.read(buffer);
                    return new String(buffer);
                }
            } catch (IOException e) {
              throw new RuntimeException(e);
            } finally {
                ActiveRecord.close(reader);
            }
        }
        return null;
    }
}

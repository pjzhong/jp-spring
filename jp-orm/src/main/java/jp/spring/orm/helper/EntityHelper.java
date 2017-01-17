package jp.spring.orm.helper;

import jp.spring.ioc.util.JpUtils;
import jp.spring.ioc.util.StringUtils;
import jp.spring.orm.annotation.Column;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 1/16/2017.
 */
public class EntityHelper{
    private static final Map<Class<?>, Map<String, String>> entityMap = new HashMap<Class<?>, Map<String, String>>();

    public static void register(Class<?> clazz) {
        if(clazz == null || entityMap.get(clazz) != null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        if(!JpUtils.isEmpty(fields)) {
            Map<String, String> fieldMap = new HashMap<>();
            for(Field field : fields) {
                String fieldName = field.getName();
                String columnName;

                if(field.isAnnotationPresent(Column.class)) {
                    columnName = field.getAnnotation(Column.class).value();
                } else {
                    columnName = StringUtils.toUnderline(fieldName);
                }

                if(!fieldName.equals(columnName)) {
                    fieldMap.put(columnName, fieldName);
                }
            }

            if(!fieldMap.isEmpty()) {
                entityMap.put(clazz, fieldMap);
            }
        }
    }

    public static Map<String, String> getFieldMap(Class<?> clazz) {
        register(clazz);
        return entityMap.get(clazz);
    }
}

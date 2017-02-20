package jp.spring.ioc.util;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Administrator on 2/19/2017.
 */
public class TypeConvertUtils {

    public static <T> T convertToBasic(Object value, Class<T> requiredType) throws IllegalArgumentException {
        if(requiredType.isInstance(value)) {
            return (T) value;
        }

        Object convertedValue = value;
        String strValue = String.valueOf(value);
        if(String.class.isAssignableFrom(requiredType)){
            convertedValue = strValue;
        } else if(Integer.TYPE.equals(requiredType) || Integer.class.equals(requiredType)) {
            convertedValue =  Integer.valueOf(strValue);
        } else if(Long.TYPE.equals(requiredType) || Long.class.equals(requiredType)) {
            convertedValue =  Long.valueOf(strValue);
        } else if(Float.TYPE.equals(requiredType) || Float.class.equals(requiredType)) {
            convertedValue =  Float.valueOf(strValue);
        }  else if(Double.TYPE.equals(requiredType) || Double.class.equals(requiredType)) {
            convertedValue =  Double.valueOf(strValue);
        } else if(Character.TYPE.equals(requiredType) || char.class.equals(requiredType)) {
            if(!StringUtils.isEmpty(strValue)) { convertedValue = strValue.charAt(0); }
        } else if(Boolean.TYPE.equals(requiredType) || Boolean.class.equals(requiredType)) {
           strValue = strValue.toLowerCase();
            if("y".equalsIgnoreCase(strValue) || "yes".equalsIgnoreCase(strValue) || "true".equalsIgnoreCase(strValue) || "1".equalsIgnoreCase(strValue)) {
                convertedValue = true;
            } else if ("n".equalsIgnoreCase(strValue) || "no".equalsIgnoreCase(strValue) || "false".equalsIgnoreCase(strValue) || "0".equalsIgnoreCase(strValue)) {
                convertedValue = false;
            }
        }

        return (T) convertedValue;
    }

    /***
     *  convert to Collection or Array;
     *
     *  @param value the value of this collection or Array
     *  @param collectionType the Type of this collection or Array
     *  @param genericType the type of value in this collection
     */
    public static <T> T convertToCollection(Object value, Class<T> collectionType, Class<?> genericType) throws IllegalArgumentException {
        Class<?> clazz = null;
        Object convertedValue = null;

        if(collectionType.isArray()) { // is Array
            clazz = collectionType.getComponentType();
            if(value.getClass().isArray()) {
                int length = Array.getLength(value);
                convertedValue = Array.newInstance(clazz, length);
                for(int i = 0; i < length; i++) {
                    Array.set(convertedValue, i , convertToBasic(Array.get(value, i), clazz));
                }
            } else if(Collection.class.isAssignableFrom(value.getClass())) {
                Collection collection = (Collection) value;
                Iterator iterator = collection.iterator();
                int length = collection.size();
                convertedValue = Array.newInstance(clazz, length);
                for(int i = 0; i < length; i++) {
                    Array.set(convertedValue, i, convertToBasic(iterator.next(), clazz));
                }
            } else if (JpUtils.isSimpleType(value.getClass())) {
                convertedValue = Array.newInstance(clazz, 1);
                Array.set(convertedValue, 0, convertToBasic(value, clazz));
            }
        } else if(Collection.class.isAssignableFrom(collectionType)) { // is Collection
            clazz = genericType;
            //create collection
            if(LinkedList.class.isAssignableFrom(collectionType)) {
                convertedValue = new LinkedList();
            } else if(List.class.isAssignableFrom(collectionType)) {
                convertedValue = new ArrayList<>();
            } else if(SortedSet.class.isAssignableFrom(collectionType)) {
                convertedValue = new TreeSet<>();
            } else {
                convertedValue = new LinkedHashSet<>();
            }

            Collection convertCollection = (Collection) convertedValue;
            if(value.getClass().isArray()) {
                for(int i = 0; i <  Array.getLength(value); i++) {
                    convertCollection.add(convertToBasic(Array.get(value, i), clazz));
                }
            } else if(Collection.class.isAssignableFrom(value.getClass())) {
                Collection collection = (Collection) value;
                Iterator iterator = collection.iterator();
                while(iterator.hasNext()) {
                    convertCollection.add(convertToBasic(iterator.next(), clazz));
                }
            }  else if(JpUtils.isSimpleType(value.getClass())){
                convertCollection.add(convertToBasic(value,clazz));
            }
        }

        return (T) convertedValue;
    }
}

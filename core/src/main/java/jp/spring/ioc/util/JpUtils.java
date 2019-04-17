package jp.spring.ioc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * Created by Administrator on 12/15/2016.
 */
public class JpUtils {
    public static boolean isBindKey(String key) {
        return key.startsWith("${") && key.endsWith("}");
    }

    public static String getBindKey(String key) {
        return key.substring(2, key.length() - 1);
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(Collection<T> c) {
        return c == null || c.isEmpty();
    }

    public static <A extends Annotation> boolean isAnnotated(Method method, Class<A> annotate) {
        return method.getAnnotation(annotate) != null;
    }

    public static <A extends Annotation> boolean isAnnotated(Class<?> clazz, Class<A> annotate) {
        if(clazz.getAnnotation(annotate) != null) {
            return true;
        }

        Annotation[] annotations = clazz.getAnnotations();
        for(Annotation annotation : annotations) {
            if(annotation.annotationType().getAnnotation(annotate) != null) {
                return true;
            }
        }

        return false;
    }

    public static <A extends Annotation> boolean isAnnotated(Field field, Class<A> annotate) {
        return field.getAnnotation(annotate) != null;
    }

    public static boolean isSimpleType(Class<?> clazz) {
        if(clazz == null) {
            return false;
        }
        return  clazz.isPrimitive() || clazz.isEnum() ||
                CharSequence.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz) ||
                clazz.equals(URI.class) || clazz.equals(URL.class) ||
                clazz.equals(Locale.class) || clazz.equals(Class.class);
    }

    public static <A> boolean isSimpleTypeArray(Class<A> clazz) {
        return isSimpleType(clazz) || (clazz.isArray() && isSimpleType(clazz.getComponentType()));
    }

    public static <A> boolean isSimpleTypeCollection(Collection<A> collection) {
       return false;
    }

    public static Object convert(String value, Class<?> targetClass) {
        try {
            if(String.class.equals(targetClass)) {
                return value;
            } else if(Integer.TYPE.equals(targetClass) || Integer.class.equals(targetClass)) {
                if(value == null) { return 0; }
                return Integer.valueOf(value);
            }  else if(Long.TYPE.equals(targetClass) || Long.class.equals(targetClass)) {
                if (value == null) {
                    return 0L;
                }
                return Long.valueOf(value);
            }  else if(Float.TYPE.equals(targetClass) || Float.class.equals(targetClass)) {
                if (value == null) {
                    return 0.0F;
                }
                return Float.valueOf(value);
            }  else if(Double.TYPE.equals(targetClass) || Double.class.equals(targetClass)) {
                if (value == null) {
                    return 0.0;
                }
                return Double.valueOf(value);
            } else if(Boolean.TYPE.equals(targetClass) || Boolean.class.equals(targetClass)) {
                if(value == null) {
                    return false;
                } else {
                    value = value.toLowerCase();
                    if("y".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
                        return true;
                    } else if ("n".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
           throw new RuntimeException("Covert failed", e);
        }
        return value;
    }

    public static <A extends Annotation> List<Method> findMethods(Class<?> clazz, Class<A> annotation) {
        Method[] methods = clazz.getDeclaredMethods();
        if(JpUtils.isEmpty(methods)) {
            return null;
        }
        List<Method> list = new LinkedList<>();
        for(Method method : methods) {
            if(JpUtils.isAnnotated(method, annotation)) {
                list.add(method);
            }
        }
        return list;
    }


    public static Method findMethod(Class<?> clazz, String name) {
        if(clazz != null && name != null) {
            Method[] methods = (clazz.isInterface() ? clazz.getMethods() : clazz.getDeclaredMethods());
            for(Method method : methods) {
                if(name.equals(method.getName())) {
                    return method;
                }
            }
        }

        return  null;
    }
}
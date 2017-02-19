package jp.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Administrator on 2/18/2017.
 */
public class ParamTest {

    @Test
    public void genericTest() throws Exception {
        List<Integer> list = new ArrayList<Integer>();

        Type type = list.getClass().getGenericSuperclass();
        if(type instanceof ParameterizedType) {
           Class<?> clzz = list.getClass();;
        }
    }

    @Test
    public void test() {
      /*  Map<String, Object> map = new HashMap<>();
        map.put("categoryId", "37");
        map.put("tagIds",Arrays.asList("17"));
        map.put("listIds",Arrays.asList("17", "45", "78","456465465"));

        JSONObject json = new JSONObject();
        json.putAll(map);

        ArticleQuery query = JSON.toJavaObject(json, ArticleQuery.class);
        System.out.println(JSON.toJSON(query));*/

        Method[] methods = ParamTest.class.getDeclaredMethods();

        for(int i = 0; i < methods.length ; i++) {
            if(i > 0) {
                System.out.println(methods[i]);
                try {
                    System.out.println("parameter:" + methods[i].getParameterTypes()[0]);
                } catch (Exception e) {}
                try {
                    System.out.println("generic:" + methods[i].getGenericParameterTypes()[0]);
                } catch (Exception e) {}

                System.out.println();
            }
        }
    }

    public void test1(Integer one ) {

    }

    public <T> void test2(T one) {

    }

    public <T> void test3(T... one) {

    }


    public <T> void test4(List<Integer> one) {

    }



}

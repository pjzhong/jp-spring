package jp.spring.ioc;

import jp.spring.ioc.stereotype.Component;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.ioc.stereotype.Service;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 1/8/2017.
 */
@Service("123456")
public class zjp {

    public String test;

    public void TheSimplestScan() {
        System.out.println("TheSimplestScan");
    }

    @Test
    public void test() {
        Controller one =  zjp.class.getAnnotation(Controller.class);
        Component two =  zjp.class.getAnnotation(Component.class);

        Field[] fields = zjp.class.getFields();

        for(Field field : fields) {
            System.out.println(field.getType());
            System.out.println(field.getName());
        }

    }
}

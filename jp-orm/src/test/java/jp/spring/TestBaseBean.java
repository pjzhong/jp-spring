package jp.spring;

import jp.spring.orm.base.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/16/2017.
 */
public class TestBaseBean extends BaseBean {

    private String name = "pj_zhong";

    private int age = 22;

    private List<String> list = new ArrayList<>();


    public static void main(String[] args) {
        /*TestBaseBean testBaseBean = new TestBaseBean();

        testBaseBean.list.add("Amazing");
        testBaseBean.list.add("excellent");

        System.out.println(testBaseBean);*/

        ProductService service = CGlibProxy.getInstance().getProxy(ProductService.class);

        System.out.println(service.getProduct(3));
    }

}

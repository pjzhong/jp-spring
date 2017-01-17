package jp.spring;

import jp.spring.model.Product;
import jp.spring.orm.helper.ConfigHelper;
import jp.spring.orm.helper.DBHelper;
import org.junit.Test;

/**
 * Created by Administrator on 1/17/2017.
 */
public class ProductService {

    @Test
    public void test() {
        System.out.println(getProduct(2));
    }

    public Product getProduct(int productId) {
        String sql = ConfigHelper.getStringProperty("select.product.id");
        return DBHelper.queryBean(Product.class, sql, productId);
    }
}

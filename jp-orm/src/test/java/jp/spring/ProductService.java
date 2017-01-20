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
        getProduct(1);
    }

    public Product getProduct(int productId) {
        String sql = ConfigHelper.getStringProperty("insert.product");

        Product product = new Product();
        product.setCode("MP010");
        product.setName("IPhone 6s plush");
        product.setProductTypeId(2);
        product.setDescription("IPhone 6s 尊贵独享版");
        product.setPrice(100000);

        DBHelper.update(sql, product);

        return product;
    }
}

package com.jp.service;

import com.jp.Model.Product;
import jp.spring.ioc.stereotype.Service;
import jp.spring.orm.helper.ConfigHelper;
import jp.spring.orm.helper.DBHelper;

/**
 * Created by Administrator on 1/17/2017.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Product getProduct(int productId) {
        String sql = ConfigHelper.getStringProperty("select.product.id");
        return DBHelper.queryBean(Product.class, sql, productId);
    }
}

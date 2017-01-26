package com.jp.service;

import com.jp.Model.Product;
import jp.spring.ioc.stereotype.Service;
import jp.spring.orm.utils.DataSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 1/17/2017.
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public Product getProduct(int productId) {
        return DataSet.select(Product.class, " id = ?", productId);

    }

    @Override
    public List<Product> getProductList() {
        return DataSet.selectList(Product.class, "");
    }

    @Override
    public boolean createProduct(Product product) {
/*        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("price", product.getPrice());
        fieldMap.put("code", product.getCode());
        fieldMap.put("name", product.getName());
        fieldMap.put("productTypeId", product.getProductTypeId());
        fieldMap.put("description", product.getDescription());

        return DataSet.insert(Product.class, fieldMap);*/
        return false;
    }

    @Override
    public boolean updateProduct(Product product) {
        return false;
    }

    @Override
    public boolean deleteProduct(int id) {
        return DataSet.delete(Product.class, "id = ?",  id);
    }
}

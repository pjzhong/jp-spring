package com.jp.service;

import com.jp.Model.Product;

import java.util.List;

/**
 * Created by Administrator on 1/17/2017.
 */
public interface ProductService {

    Product getProduct(int ProductId);

    List<Product> getProductList();

    boolean createProduct(Product product);

    boolean updateProduct(Product product);

    boolean deleteProduct(int id);
}

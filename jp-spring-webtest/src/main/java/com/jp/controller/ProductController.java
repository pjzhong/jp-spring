package com.jp.controller;

import com.jp.Model.Product;
import com.jp.service.ProductService;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Administrator on 1/18/2017.
 */
@Controller
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping("/products")
    @ResponseBody
    public List<Product> getProducts() {
        List<Product> productList = productService.getProductList();
        return productList;
    }

    @RequestMapping("/deletion")
    @ResponseBody
    public boolean deleteProduct(Integer productId) {
       return productService.deleteProduct(productId);
    }
}

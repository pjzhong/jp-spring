package com.jp.controller;

import com.jp.Model.Product;
import com.jp.service.ProductService;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.ResponseBody;
import jp.spring.web.context.ProcessContext;
import jp.spring.web.support.MultipartFile;
import jp.spring.web.support.MultipartFiles;

import java.util.List;

/**
 * Created by Administrator on 1/18/2017.
 * waiting for orm module finished
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

    @RequestMapping(value = "/products/create", method = RequestMethod.GET)
    public String create() {
        return "products_create";
    }

    @RequestMapping(value = "/products/create", method = RequestMethod.POST)
    public Product create(Product product, MultipartFiles files) {
        for(MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
        }
        System.out.println(product);
        return product;
    }

    @RequestMapping("/deletion")
    @ResponseBody
    public boolean deleteProduct(Integer productId) {
       return productService.deleteProduct(productId);
    }
}

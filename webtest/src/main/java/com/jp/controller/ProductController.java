package com.jp.controller;

import com.jp.Model.Product;
import com.jp.service.ProductService;
import jp.spring.ioc.beans.factory.annotation.Autowired;
import jp.spring.ioc.stereotype.Controller;
import jp.spring.mvc.annotation.RequestMapping;
import jp.spring.mvc.annotation.RequestMethod;
import jp.spring.mvc.annotation.ResponseBody;
import jp.spring.mvc.support.MultipartFile;
import jp.spring.mvc.support.MultipartFiles;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

    @RequestMapping(value = {"/products/create", "/products/create2"}, method = RequestMethod.GET)
    public String create() {
        return "products_create";
    }

    @RequestMapping(value = "/products/create", method = RequestMethod.POST)
    public Product create(Product product, MultipartFiles files) throws Exception {
        System.out.println(product);
        if(files != null) {
            String rootPath = System.getProperty("catalina.home");
            File dir = new File(rootPath + File.separator + "tempFiles");
            if(!dir.exists()) {
                dir.mkdirs();
            }
            File serverFile;
            BufferedOutputStream stream;
            for(MultipartFile file : files) {
                serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(file.getBytes());
                stream.close();
                System.out.println("Server File Location=" + serverFile.getAbsolutePath());
            }
        }
        return product;
    }

    @RequestMapping("/deletion")
    @ResponseBody
    public boolean deleteProduct(Integer productId) {
       return productService.deleteProduct(productId);
    }
}

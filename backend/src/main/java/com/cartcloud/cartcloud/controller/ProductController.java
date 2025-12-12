package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;   

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        Product p = new Product();
        p.setProductId(1L);
        p.setName("Test product");
        p.setDescription("Demo");
        p.setPrice(BigDecimal.valueOf(9.99)); 

        products.add(p);
        return products;
    }
}

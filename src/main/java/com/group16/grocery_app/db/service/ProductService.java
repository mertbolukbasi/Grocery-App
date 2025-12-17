package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.ProductRepository;

public class ProductService {

    private ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }
}

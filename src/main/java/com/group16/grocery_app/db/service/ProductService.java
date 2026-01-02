package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.ProductRepository;
import com.group16.grocery_app.model.Product;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.SQLException;

public class ProductService {

    private ProductRepository productRepository;

    public ProductService() {
        this.productRepository = new ProductRepository();
    }
    public ObservableList<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public boolean addProduct(String name, com.group16.grocery_app.model.ProductType type, double price, double stock, double threshold, java.io.File imageFile) {
        try {
            return productRepository.addProduct(name, type, price, stock, threshold, imageFile);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(int productId, String name, com.group16.grocery_app.model.ProductType type, double price, double stock, double threshold, java.io.File imageFile) {
        try {
            return productRepository.updateProduct(productId, name, type, price, stock, threshold, imageFile);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeProduct(int productId) {
        try {
            return productRepository.removeProduct(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
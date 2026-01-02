package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.ProductRepository;
import com.group16.grocery_app.model.Product;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.SQLException;

/**
 * Service class for product-related operations.
 * Handles product CRUD operations and inventory management.
 *
 * @author Mert Bölükbaşı
 */
public class ProductService {

    private ProductRepository productRepository;

    /**
     * Creates a new ProductService instance.
     *
     * @author Mert Bölükbaşı
     */
    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    /**
     * Gets all products from the database.
     *
     * @return list of all products, empty list on error
     * @author Mert Bölükbaşı
     */
    public ObservableList<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Adds a new product to the database.
     *
     * @param name      product name
     * @param type      product type (FRUIT or VEGETABLE)
     * @param price     product price
     * @param stock     initial stock quantity
     * @param threshold stock threshold for price doubling
     * @param imageFile product image file
     * @return true if product added successfully
     * @author Mert Bölükbaşı
     */
    public boolean addProduct(String name, com.group16.grocery_app.model.ProductType type, double price, double stock, double threshold, java.io.File imageFile) {
        try {
            return productRepository.addProduct(name, type, price, stock, threshold, imageFile);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing product's information.
     *
     * @param productId product ID to update
     * @param name      new product name
     * @param type      new product type
     * @param price     new price
     * @param stock     new stock quantity
     * @param threshold new threshold value
     * @param imageFile new product image file
     * @return true if update successful
     * @author Mert Bölükbaşı
     */
    public boolean updateProduct(int productId, String name, com.group16.grocery_app.model.ProductType type, double price, double stock, double threshold, java.io.File imageFile) {
        try {
            return productRepository.updateProduct(productId, name, type, price, stock, threshold, imageFile);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a product from the database.
     *
     * @param productId product ID to remove
     * @return true if removal successful
     * @author Mert Bölükbaşı
     */
    public boolean removeProduct(int productId) {
        try {
            return productRepository.removeProduct(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
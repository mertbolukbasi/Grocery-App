package com.group16.grocery_app.model;

import javafx.scene.image.Image;

/**
 * Represents a product in the grocery store.
 * Handles pricing logic based on stock levels and thresholds.
 *
 * @author Mert Bölükbaşı
 */
public class Product {
    private int id;
    private String name;
    private ProductType type;
    private double price;
    private double stock;
    private double threshold;
    private Image image;

    /**
     * Creates a new product with all required information.
     *
     * @param id unique product identifier
     * @param name product name
     * @param type product type (FRUIT or VEGETABLE)
     * @param price base price
     * @param stock current stock quantity
     * @param threshold stock threshold for price doubling
     * @param image product image
     * @author Mert Bölükbaşı
     */
    public Product(int id, String name, ProductType type, double price, double stock, double threshold, Image image) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.stock = stock;
        this.threshold = threshold;
        this.image = image;
    }

    /**
     * Calculates the effective price based on current stock.
     * Price doubles when stock falls below threshold.
     *
     * @return effective price (doubled if stock <= threshold)
     * @author Mert Bölükbaşı
     */
    public double getEffectivePrice() {
        if (stock <= threshold) {
            return price * 2;
        }
        return price;
    }

    /**
     * Calculates effective price considering items already in cart.
     * Price doubles if remaining stock (after cart) falls below threshold.
     *
     * @param cartQuantity quantity of this product in the cart
     * @return effective price (doubled if remaining stock <= threshold)
     * @author Mert Bölükbaşı
     */
    public double getEffectivePrice(double cartQuantity) {
        double remainingStock = stock - cartQuantity;
        if (remainingStock <= threshold) {
            return price * 2;
        }
        return price;
    }

    /**
     * Gets the product ID.
     *
     * @return product ID
     * @author Mert Bölükbaşı
     */
    public int getId() { return id; }

    /**
     * Gets the product name.
     *
     * @return product name
     * @author Mert Bölükbaşı
     */
    public String getName() { return name; }

    /**
     * Gets the product type.
     *
     * @return product type
     * @author Mert Bölükbaşı
     */
    public ProductType getType() { return type; }

    /**
     * Gets the base price.
     *
     * @return base price
     * @author Mert Bölükbaşı
     */
    public double getPrice() { return price; }

    /**
     * Gets the current stock quantity.
     *
     * @return stock quantity
     * @author Mert Bölükbaşı
     */
    public double getStock() { return stock; }

    /**
     * Gets the stock threshold for price doubling.
     *
     * @return threshold value
     * @author Mert Bölükbaşı
     */
    public double getThreshold() { return threshold; }

    /**
     * Gets the product image.
     *
     * @return product image
     * @author Mert Bölükbaşı
     */
    public Image getImage() { return image; }

    /**
     * Sets the product price.
     *
     * @param price new price
     * @author Mert Bölükbaşı
     */
    public void setPrice(double price) { this.price = price; }

    /**
     * Sets the stock quantity.
     *
     * @param stock new stock quantity
     * @author Mert Bölükbaşı
     */
    public void setStock(double stock) { this.stock = stock; }

    /**
     * Sets the stock threshold.
     *
     * @param threshold new threshold value
     * @author Mert Bölükbaşı
     */
    public void setThreshold(double threshold) { this.threshold = threshold; }
}
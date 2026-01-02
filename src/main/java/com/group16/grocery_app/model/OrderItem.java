package com.group16.grocery_app.model;

/**
 * Represents a single item line within an order.
 * Contains the product details, quantity purchased, and the price at that moment.
 * @author Oğuzhan Aydın
 */
public class OrderItem {

    private Product product;
    private double quantity;
    private double unitPrice;

    /**
     * Creates a new order item.
     * @param product The product being purchased.
     * @param quantity The amount of the product (e.g., in kg or units).
     * @param unitPrice The price per unit at the time of purchase.
     * @author Oğuzhan Aydın
     */
    public OrderItem(Product product, double quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Gets the product associated with this item.
     * @return The product object.
     * @author Oğuzhan Aydın
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the quantity purchased.
     * @return The quantity amount.
     * @author Oğuzhan Aydın
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Gets the price per unit for this item.
     * @return The unit price.
     * @author Oğuzhan Aydın
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Calculates the total cost for this line item.
     * @return The total price (quantity multiplied by unit price).
     * @author Oğuzhan Aydın
     */
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}

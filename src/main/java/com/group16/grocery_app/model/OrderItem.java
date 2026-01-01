package com.group16.grocery_app.model;

public class OrderItem {

    private Product product;
    private double quantity;
    private double unitPrice;

    public OrderItem(Product product, double quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}

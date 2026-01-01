package com.group16.grocery_app.model;

public class CartItem {

    private Product product;
    private double quantity;
    private Cart cart;

    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public CartItem(Product product, double quantity, Cart cart) {
        this.product = product;
        this.quantity = quantity;
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

    public void addQuantity(double amount) {
        this.quantity+=amount;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public double getTotalPrice() {
        double cartQuantity = cart != null ? cart.getQuantityOfProduct(product) : quantity;
        double effectivePrice = product.getEffectivePrice(cartQuantity);
        return quantity * effectivePrice;
    }

    public double getEffectivePrice() {
        double cartQuantity = cart != null ? cart.getQuantityOfProduct(product) : quantity;
        return product.getEffectivePrice(cartQuantity);
    }

    public void setQuantity(double quantity) {
        this.quantity=quantity;
    }
}

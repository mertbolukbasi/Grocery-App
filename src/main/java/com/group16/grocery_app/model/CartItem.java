package com.group16.grocery_app.model;

public class CartItem {

    private Product product;
    private double quantity;
    private Cart cart; // Reference to cart to calculate effective price based on cart quantities

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
        // Calculate effective price based on remaining stock (after considering cart quantities)
        double cartQuantity = cart != null ? cart.getQuantityOfProduct(product) : quantity;
        double effectivePrice = product.getEffectivePrice(cartQuantity);
        return quantity * effectivePrice;
    }

    /**
     * Gets the effective price per unit for this cart item.
     * Price is doubled if remaining stock (after cart quantities) <= threshold.
     */
    public double getEffectivePrice() {
        double cartQuantity = cart != null ? cart.getQuantityOfProduct(product) : quantity;
        return product.getEffectivePrice(cartQuantity);
    }

    public void setQuantity(double quantity) {
        this.quantity=quantity;
    }
}

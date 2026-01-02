package com.group16.grocery_app.model;

/**
 * Model class representing an item in the shopping cart.
 * Stores product information, quantity, and calculates item pricing.
 *
 * @author Ege Usug
 */
public class CartItem {

    private Product product;
    private double quantity;
    private Cart cart;

    /**
     * Creates a new cart item.
     *
     * @param product The product
     * @param quantity The quantity
     * @author Ege Usug
     */
    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Creates a new cart item with a cart reference.
     *
     * @param product The product
     * @param quantity The quantity
     * @param cart The cart reference
     * @author Ege Usug
     */
    public CartItem(Product product, double quantity, Cart cart) {
        this.product = product;
        this.quantity = quantity;
        this.cart = cart;
    }

    /**
     * Gets the product.
     *
     * @return The product
     * @author Ege Usug
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the quantity.
     *
     * @return The quantity
     * @author Ege Usug
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Adds to the quantity.
     *
     * @param amount The amount to add
     * @author Ege Usug
     */
    public void addQuantity(double amount) {
        this.quantity+=amount;
    }

    /**
     * Sets the cart reference.
     *
     * @param cart The cart
     * @author Ege Usug
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * Calculates the total price for this cart item.
     *
     * @return The total price (quantity × effective price)
     * @author Ege Usug
     */
    public double getTotalPrice() {
        double cartQuantity = cart != null ? cart.getQuantityOfProduct(product) : quantity;
        double effectivePrice = product.getEffectivePrice(cartQuantity);
        return quantity * effectivePrice;
    }

    /**
     * Gets the effective price per unit based on stock levels.
     *
     * @return The effective price per unit
     * @author Ege Usug
     */
    public double getEffectivePrice() {
        double cartQuantity = cart != null ? cart.getQuantityOfProduct(product) : quantity;
        return product.getEffectivePrice(cartQuantity);
    }

    /**
     * Sets the quantity.
     *
     * @param quantity The new quantity
     * @author Ege Usug
     */
    public void setQuantity(double quantity) {
        this.quantity=quantity;
    }
}

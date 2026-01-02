package com.group16.grocery_app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a shopping cart.
 * Manages cart items, discounts, and checkout operations.
 *
 * @author Ege Usug
 */
public class Cart {

    private ObservableList<CartItem> items = FXCollections.observableArrayList();
    private double couponDiscount = 0.0;
    private double loyaltyDiscount = 0.0;
    private String appliedCouponCode = null;

    /**
     * Adds a product to the cart or updates quantity if already present.
     *
     * @param product The product to add
     * @param quantity The quantity to add
     * @author Ege Usug
     */
    public void addProduct(Product product, double quantity) {

        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.addQuantity(quantity);
                refreshEffectivePrices();
                return;
            }
        }

        CartItem newItem = new CartItem(product, quantity, this);
        items.add(newItem);
        refreshEffectivePrices();
    }

    /**
     * Refreshes effective prices for all cart items.
     *
     * @author Ege Usug
     */
    public void refreshEffectivePrices() {
        for (CartItem item : items) {
            item.setCart(this);
        }
    }

    /**
     * Gets the list of items in the cart.
     *
     * @return ObservableList of cart items
     * @author Ege Usug
     */
    public ObservableList<CartItem> getItems() {
        return items;
    }

    /**
     * Gets the quantity of a specific product in the cart.
     *
     * @param product The product to check
     * @return The quantity of the product, or 0.0 if not found
     * @author Ege Usug
     */
    public double getQuantityOfProduct(Product product) {

        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                return item.getQuantity();
            }
        }

        return 0.0;
    }

    /**
     * Calculates the total price of all items in the cart.
     *
     * @return The total price
     * @author Ege Usug
     */
    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    /**
     * Gets the subtotal (same as total before discounts).
     *
     * @return The subtotal
     * @author Ege Usug
     */
    public double getSubtotal() {
        return getTotal();
    }

    /**
     * Gets the coupon discount amount.
     *
     * @return The coupon discount amount
     * @author Ege Usug
     */
    public double getCouponDiscount() {
        return couponDiscount;
    }

    /**
     * Sets the coupon discount and code.
     *
     * @param discount The discount amount
     * @param couponCode The coupon code
     * @author Ege Usug
     */
    public void setCouponDiscount(double discount, String couponCode) {
        this.couponDiscount = discount;
        this.appliedCouponCode = couponCode;
    }

    /**
     * Clears the coupon discount.
     *
     * @author Ege Usug
     */
    public void clearCouponDiscount() {
        this.couponDiscount = 0.0;
        this.appliedCouponCode = null;
    }

    /**
     * Gets the applied coupon code.
     *
     * @return The coupon code, or null if no coupon applied
     * @author Ege Usug
     */
    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    /**
     * Gets the loyalty discount amount.
     *
     * @return The loyalty discount amount
     * @author Ege Usug
     */
    public double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    /**
     * Sets the loyalty discount amount.
     *
     * @param discount The discount amount
     * @author Ege Usug
     */
    public void setLoyaltyDiscount(double discount) {
        this.loyaltyDiscount = discount;
    }

    /**
     * Calculates the total after applying coupon and loyalty discounts.
     *
     * @return The total after discounts (minimum 0)
     * @author Ege Usug
     */
    public double getTotalAfterDiscounts() {
        double subtotal = getSubtotal();
        double afterCoupon = subtotal - couponDiscount;
        double afterLoyalty = afterCoupon - loyaltyDiscount;
        return Math.max(0, afterLoyalty);
    }

    /**
     * Removes a product from the cart.
     *
     * @param product The product to remove
     * @author Ege Usug
     */
    public void removeProduct(Product product) {
        items.removeIf(item -> item.getProduct().getId() == product.getId());
        refreshEffectivePrices();
    }

    /**
     * Creates an order from the cart with VAT applied.
     *
     * @param vatRate The VAT rate to apply
     * @return The created order
     * @author Ege Usug
     */
    public Order checkout(double vatRate) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : items) {
            orderItems.add(new OrderItem(item.getProduct(), item.getQuantity(), item.getEffectivePrice()));
        }
        double subtotal = getTotal();
        double totalWithVAT = subtotal * (1 + vatRate);
        Order order = new Order(orderItems, totalWithVAT);
        return order;
    }

    /**
     * Creates an order from the cart with VAT and delivery date, applying discounts.
     *
     * @param vatRate The VAT rate to apply
     * @param deliveryDate The delivery date and time
     * @return The created order
     * @author Ege Usug
     */
    public Order checkout(double vatRate, java.time.LocalDateTime deliveryDate) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : items) {
            orderItems.add(new OrderItem(item.getProduct(), item.getQuantity(), item.getEffectivePrice()));
        }
        double afterDiscounts = getTotalAfterDiscounts();
        double totalWithVAT = afterDiscounts * (1 + vatRate);
        Order order = new Order(orderItems, totalWithVAT);
        order.setDeliveryDate(deliveryDate);
        return order;
    }

    /**
     * Clears all items and discounts from the cart.
     *
     * @author Ege Usug
     */
    public void clear() {
        items.clear();
        couponDiscount = 0.0;
        loyaltyDiscount = 0.0;
        appliedCouponCode = null;
    }
}

package com.group16.grocery_app.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private ObservableList<CartItem> items = FXCollections.observableArrayList();
    private double couponDiscount = 0.0;
    private double loyaltyDiscount = 0.0;
    private String appliedCouponCode = null;

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

    public void refreshEffectivePrices() {
        for (CartItem item : items) {
            item.setCart(this);
        }
    }

    public ObservableList<CartItem> getItems() {
        return items;
    }

    public double getQuantityOfProduct(Product product) {

        for (CartItem item : items) {
            if (item.getProduct().getId() == product.getId()) {
                return item.getQuantity();
            }
        }

        return 0.0;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public double getSubtotal() {
        return getTotal();
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(double discount, String couponCode) {
        this.couponDiscount = discount;
        this.appliedCouponCode = couponCode;
    }

    public void clearCouponDiscount() {
        this.couponDiscount = 0.0;
        this.appliedCouponCode = null;
    }

    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }

    public double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public void setLoyaltyDiscount(double discount) {
        this.loyaltyDiscount = discount;
    }

    public double getTotalAfterDiscounts() {
        double subtotal = getSubtotal();
        double afterCoupon = subtotal - couponDiscount;
        double afterLoyalty = afterCoupon - loyaltyDiscount;
        return Math.max(0, afterLoyalty); // Don't allow negative totals
    }

    public void removeProduct(Product product) {
        items.removeIf(item -> item.getProduct().getId() == product.getId());
        refreshEffectivePrices();
    }

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

    public void clear() {
        items.clear();
        couponDiscount = 0.0;
        loyaltyDiscount = 0.0;
        appliedCouponCode = null;
    }
}

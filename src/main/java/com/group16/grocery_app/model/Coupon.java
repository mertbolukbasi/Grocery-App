package com.group16.grocery_app.model;

import java.time.LocalDate;

public class Coupon {
    private int id;
    private String code;
    private double discountAmount;
    private LocalDate expiryDate;
    private boolean isActive;

    public Coupon(int id, String code, double discountAmount, LocalDate expiryDate, boolean isActive) {
        this.id = id;
        this.code = code;
        this.discountAmount = discountAmount;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public double getDiscountAmount() { return discountAmount; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isActive() { return isActive; }

    public void setActive(boolean active) { this.isActive = active; }
}


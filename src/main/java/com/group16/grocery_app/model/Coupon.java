package com.group16.grocery_app.model;

import java.time.LocalDate;

/**
 * Model class representing a discount coupon in the system.
 * Stores coupon code, discount amount, expiry date, and active status.
 *
 * @author Yiğit Emre Ünlüçerçi
 */
public class Coupon {
    private int id;
    private String code;
    private double discountAmount;
    private LocalDate expiryDate;
    private boolean isActive;

    /**
     * Creates a new Coupon instance.
     *
     * @param id unique coupon ID
     * @param code coupon code
     * @param discountAmount discount amount of the coupon
     * @param expiryDate expiry date of the coupon
     * @param isActive whether the coupon is active
     * @author Yiğit Emre Ünlüçerçi
     */
    public Coupon(int id, String code, double discountAmount, LocalDate expiryDate, boolean isActive) {
        this.id = id;
        this.code = code;
        this.discountAmount = discountAmount;
        this.expiryDate = expiryDate;
        this.isActive = isActive;
    }

    /**
     * Returns the unique coupon ID.
     *
     * @return coupon ID
     * @author Yiğit Emre Ünlüçerçi
     */
    public int getId() { return id; }

    /**
     * Returns the coupon code.
     *
     * @return coupon code
     * @author Yiğit Emre Ünlüçerçi
     */
    public String getCode() { return code; }

    /**
     * Returns the discount amount of the coupon.
     *
     * @return discount amount
     * @author Yiğit Emre Ünlüçerçi
     */
    public double getDiscountAmount() { return discountAmount; }

    /**
     * Returns the expiry date of the coupon.
     *
     * @return expiry date (can be null if not set)
     * @author Yiğit Emre Ünlüçerçi
     */
    public LocalDate getExpiryDate() { return expiryDate; }


    /**
     * Returns whether the coupon is active.
     *
     * @return true if active, false otherwise
     * @author Yiğit Emre Ünlüçerçi
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets the active status of the coupon.
     *
     * @param active new active status
     * @author Yiğit Emre Ünlüçerçi
     */
    public void setActive(boolean active) { this.isActive = active; }
}

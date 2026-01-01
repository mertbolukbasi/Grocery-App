package com.group16.grocery_app.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private int id;
    private List<OrderItem> items;
    private double total;
    private LocalDateTime createdAt;
    private LocalDateTime deliveryDate;
    private String status;
    private int customerId;
    private Integer carrierId;
    private Integer carrierRating;

    public Order(List<OrderItem> items, double total) {
        this.items = items;
        this.total = total;
        this.createdAt = LocalDateTime.now();
        this.status = "Pending";
    }

    public Order(int id, List<OrderItem> items, double total, LocalDateTime createdAt, LocalDateTime deliveryDate, String status) {
        this.id = id;
        this.items = items;
        this.total = total;
        this.createdAt = createdAt;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Integer carrierId) {
        this.carrierId = carrierId;
    }

    public Integer getCarrierRating() {
        return carrierRating;
    }

    public void setCarrierRating(Integer carrierRating) {
        this.carrierRating = carrierRating;
    }

    public boolean hasRating() {
        return carrierRating != null && carrierRating >= 1 && carrierRating <= 5;
    }
}

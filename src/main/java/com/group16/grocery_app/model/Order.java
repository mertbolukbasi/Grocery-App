package com.group16.grocery_app.model;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private String productList;
    private String customerName;
    private String customerAddress;
    private double totalAmount;
    private LocalDateTime requestedDate;
    private String status;
    private int carrierId;

    public Order(int orderId, String customerName, String customerAddress, String productList, double totalAmount, LocalDateTime requestedDate, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.productList = productList;
        this.totalAmount = totalAmount;
        this.requestedDate = requestedDate;
        this.status = status;
    }


    public int getOrderId() { return orderId; }

    public String getProductList() { return productList; }

    public String getCustomerName() { return customerName; }

    public String getCustomerAddress() { return customerAddress; }

    public double getTotalAmount() { return totalAmount; }

    public LocalDateTime getRequestedDate() { return requestedDate; }

    public String getStatus() { return status; }

    public int getCarrierId() { return carrierId; }
}
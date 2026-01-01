package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.OrderRepository;
import com.group16.grocery_app.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.sql.SQLException;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
    }

    public int placeOrder(Order order, int customerId) {
        return placeOrder(order, customerId, null);
    }

    public void placeOrderWithInvoice(Order order, int customerId, LocalDateTime deliveryDate, String customerName, String customerAddress) {
        try {
            int orderId = placeOrder(order, customerId, deliveryDate);
            String invoiceText = com.group16.grocery_app.utils.InvoiceGenerator.generateInvoiceText(
                    order, customerName, customerAddress);
            saveInvoice(orderId, invoiceText);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Order failed");
        }
    }

    public int placeOrder(Order order, int customerId, LocalDateTime deliveryDate) {
        try {
            orderRepository.saveOrder(order, customerId, deliveryDate);
            return order.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Order failed");
        }
    }

    public ObservableList<Order> getOrdersByCustomerId(int customerId) {
        try {
            return orderRepository.getOrdersByCustomerId(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<Order> getAllOrders() {
        try {
            return orderRepository.getAllOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<Order> getOrdersByStatus(String status) {
        try {
            return orderRepository.getOrdersByStatus(status);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<Order> getAvailableOrders() {
        try {
            return orderRepository.getOrdersByStatus("Pending", true);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public ObservableList<Order> getOrdersByCarrierId(int carrierId, String status) {
        try {
            return orderRepository.getOrdersByCarrierId(carrierId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public boolean selectOrder(int orderId, int carrierId) {
        try {
            return orderRepository.selectOrder(orderId, carrierId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unselectOrder(int orderId, int carrierId) {
        try {
            return orderRepository.unselectOrder(orderId, carrierId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completeDelivery(int orderId, LocalDateTime deliveryDateTime) {
        try {
            return orderRepository.completeDelivery(orderId, deliveryDateTime);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rateCarrier(int orderId, int rating) {
        try {
            return orderRepository.rateCarrier(orderId, rating);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveInvoice(int orderId, String invoiceText) {
        try {
            return orderRepository.saveInvoice(orderId, invoiceText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getInvoice(int orderId) {
        try {
            return orderRepository.getInvoice(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getCompletedOrdersCount(int customerId) {
        try {
            return orderRepository.getCompletedOrdersCount(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean cancelOrder(int orderId, int customerId) {
        try {
            return orderRepository.cancelOrder(orderId, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getAverageCarrierRating(int carrierId) {
        try {
            return orderRepository.getAverageCarrierRating(carrierId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1.0;
        }
    }
}
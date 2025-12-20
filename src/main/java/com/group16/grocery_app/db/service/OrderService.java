package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.OrderRepository;
import com.group16.grocery_app.model.Order;

import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService() {
        this.orderRepository = new OrderRepository();
    }

    public List<Order> getAvailableOrders() {
        return orderRepository.findPendingOrders();
    }

    public List<Order> getCurrentOrders(int carrierId) {
        List<Order> allOrders = orderRepository.findOrdersByCarrier(carrierId);

        return allOrders.stream()
                .filter(order -> "Selected".equalsIgnoreCase(order.getStatus())) //burdaki ok işareti şu anlama geliyor: order selected ise yap
                .collect(Collectors.toList());
    }

    public List<Order> getOrderHistory(int carrierId) {
        List<Order> allOrders = orderRepository.findOrdersByCarrier(carrierId);

        return allOrders.stream()
                .filter(order -> "Delivered".equalsIgnoreCase(order.getStatus()))
                .collect(Collectors.toList());
    }

    public String takeOrder(int orderId, int carrierId) {
        boolean success = orderRepository.updateOrderToCarrier(orderId, carrierId);

        if (success) {
            return "SUCCESS";
        } else {
            return "FAIL: Order is no longer available.";
        }
    }

    public boolean completeDelivery(int orderId) {
        return orderRepository.completeOrder(orderId);
    }
}
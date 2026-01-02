package com.group16.grocery_app.db.service;

import com.group16.grocery_app.db.repository.OrderRepository;
import com.group16.grocery_app.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.sql.SQLException;

/**
 * Service class that acts as a bridge between the controller and the repository.
 * Handles business logic and error management for order operations.
 * @author Oğuzhan Aydın
 */
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Initializes the service with a new OrderRepository.
     * @author Oğuzhan Aydın
     */
    public OrderService() {
        this.orderRepository = new OrderRepository();
    }

    /**
     * Places a new order for a customer without a specific delivery date.
     * @param order The order details.
     * @param customerId The ID of the customer.
     * @return The ID of the newly created order.
     * @author Oğuzhan Aydın
     */
    public int placeOrder(Order order, int customerId) {
        return placeOrder(order, customerId, null);
    }

    /**
     * Places an order and automatically generates/saves the invoice text.
     * @param order The order object.
     * @param customerId The customer ID.
     * @param deliveryDate The scheduled delivery date.
     * @param customerName The name of the customer for the invoice.
     * @param customerAddress The address of the customer for the invoice.
     * @throws RuntimeException If the order placement fails.
     * @author Oğuzhan Aydın
     */
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

    /**
     * Places a new order for a customer without a specific delivery date.
     * @param order The order details.
     * @param customerId The ID of the customer.
     * @return The ID of the newly created order.
     * @author Oğuzhan Aydın
     */
    public int placeOrder(Order order, int customerId, LocalDateTime deliveryDate) {
        try {
            orderRepository.saveOrder(order, customerId, deliveryDate);
            return order.getId();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Order failed");
        }
    }

    /**
     * Retrieves all orders belonging to a specific customer.
     * @param customerId The customer's ID.
     * @return A list of the customer's orders.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByCustomerId(int customerId) {
        try {
            return orderRepository.getOrdersByCustomerId(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Retrieves every order in the system.
     * @return A list of all orders.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getAllOrders() {
        try {
            return orderRepository.getAllOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Retrieves orders filtered by a specific status.
     * @param status The status string (e.g., "Pending").
     * @return A list of matching orders.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByStatus(String status) {
        try {
            return orderRepository.getOrdersByStatus(status);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Retrieves orders that are "Pending" and not yet assigned to any carrier.
     * @return A list of available orders for carriers.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getAvailableOrders() {
        try {
            return orderRepository.getOrdersByStatus("Pending", true);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Retrieves orders assigned to a specific carrier.
     * @param carrierId The carrier's ID.
     * @param status Optional filter for order status.
     * @return A list of orders for the carrier.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByCarrierId(int carrierId, String status) {
        try {
            return orderRepository.getOrdersByCarrierId(carrierId, status);
        } catch (SQLException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Assigns a specific order to a carrier.
     * @param orderId The order ID.
     * @param carrierId The carrier ID.
     * @return True if assignment was successful.
     * @author Oğuzhan Aydın
     */
    public boolean selectOrder(int orderId, int carrierId) {
        try {
            return orderRepository.selectOrder(orderId, carrierId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a carrier assignment from an order (Unselect).
     * @param orderId The order ID.
     * @param carrierId The carrier ID.
     * @return True if unselection was successful.
     * @author Oğuzhan Aydın
     */
    public boolean unselectOrder(int orderId, int carrierId) {
        try {
            return orderRepository.unselectOrder(orderId, carrierId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marks an order as delivered.
     * @param orderId The order ID.
     * @param deliveryDateTime The time of delivery.
     * @return True if update was successful.
     * @author Oğuzhan Aydın
     */
    public boolean completeDelivery(int orderId, LocalDateTime deliveryDateTime) {
        try {
            return orderRepository.completeDelivery(orderId, deliveryDateTime);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Submits a rating for a carrier service.
     * @param orderId The order ID.
     * @param rating The rating value (1-5).
     * @return True if rating was saved successfully.
     * @author Oğuzhan Aydın
     */
    public boolean rateCarrier(int orderId, int rating) {
        try {
            return orderRepository.rateCarrier(orderId, rating);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the text content of an invoice to the database.
     * @param orderId The order ID.
     * @param invoiceText The invoice content.
     * @return True if saved successfully.
     * @author Oğuzhan Aydın
     */
    public boolean saveInvoice(int orderId, String invoiceText) {
        try {
            return orderRepository.saveInvoice(orderId, invoiceText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the stored invoice text for an order.
     * @param orderId The order ID.
     * @return The invoice text, or null if not found.
     * @author Oğuzhan Aydın
     */
    public String getInvoice(int orderId) {
        try {
            return orderRepository.getInvoice(orderId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the count of completed orders for a customer (for loyalty calculation).
     * @param customerId The customer ID.
     * @return The number of delivered orders.
     * @author Oğuzhan Aydın
     */
    public int getCompletedOrdersCount(int customerId) {
        try {
            return orderRepository.getCompletedOrdersCount(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Cancels a pending order.
     * @param orderId The order ID.
     * @param customerId The customer ID (for verification).
     * @return True if cancelled successfully.
     * @author Oğuzhan Aydın
     */
    public boolean cancelOrder(int orderId, int customerId) {
        try {
            return orderRepository.cancelOrder(orderId, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculates the average rating for a carrier.
     * @param carrierId The carrier ID.
     * @return The average rating score.
     * @author Oğuzhan Aydın
     */
    public double getAverageCarrierRating(int carrierId) {
        try {
            return orderRepository.getAverageCarrierRating(carrierId);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1.0;
        }
    }
}
package com.group16.grocery_app.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a customer order in the grocery application.
 * Contains details about purchased items, order status, delivery info, and involved users.
 * @author Oğuzhan Aydın
 */
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

    /**
     * Constructor for creating a new order before saving it to the database.
     * Sets the creation time to now and status to 'Pending'.
     * @param items The list of items in the order.
     * @param total The total cost of the order.
     * @author Oğuzhan Aydın
     */
    public Order(List<OrderItem> items, double total) {
        this.items = items;
        this.total = total;
        this.createdAt = LocalDateTime.now();
        this.status = "Pending";
    }

    /**
     * Constructor for reconstructing an existing order from the database.
     * @param id The unique order ID.
     * @param items The list of items.
     * @param total The total cost.
     * @param createdAt The date the order was placed.
     * @param deliveryDate The date the order was delivered.
     * @param status The current status of the order.
     * @author Oğuzhan Aydın
     */
    public Order(int id, List<OrderItem> items, double total, LocalDateTime createdAt, LocalDateTime deliveryDate, String status) {
        this.id = id;
        this.items = items;
        this.total = total;
        this.createdAt = createdAt;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    /**
     * Gets the unique ID of the order.
     * @return The order ID.
     * @author Oğuzhan Aydın
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique ID of the order.
     * @param id The new order ID.
     * @author Oğuzhan Aydın
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the list of items included in this order.
     * @return A list of OrderItem objects.
     * @author Oğuzhan Aydın
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * Gets the total cost of the order.
     * @return The total price.
     * @author Oğuzhan Aydın
     */
    public double getTotal() {
        return total;
    }

    /**
     * Gets the timestamp when the order was created.
     * @return The creation date and time.
     * @author Oğuzhan Aydın
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the order was created.
     * @param createdAt The creation date and time.
     * @author Oğuzhan Aydın
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the delivery date and time.
     * @return The delivery timestamp, or null if not delivered yet.
     * @author Oğuzhan Aydın
     */
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Sets the delivery date and time.
     * @param deliveryDate The delivery timestamp.
     * @author Oğuzhan Aydın
     */
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * Gets the current status of the order (e.g., Pending, Selected, Delivered).
     * @return The status string.
     * @author Oğuzhan Aydın
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the order.
     * @param status The new status string.
     * @author Oğuzhan Aydın
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the ID of the customer who placed the order.
     * @return The customer ID.
     * @author Oğuzhan Aydın
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the ID of the customer who placed the order.
     * @param customerId The customer ID.
     * @author Oğuzhan Aydın
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the ID of the carrier assigned to this order.
     * @return The carrier ID, or null if not assigned.
     * @author Oğuzhan Aydın
     */
    public Integer getCarrierId() {
        return carrierId;
    }

    /**
     * Sets the ID of the carrier assigned to this order.
     * @param carrierId The carrier ID.
     * @author Oğuzhan Aydın
     */
    public void setCarrierId(Integer carrierId) {
        this.carrierId = carrierId;
    }

    /**
     * Gets the rating given to the carrier for this order.
     * @return The rating (1-5), or null if not rated.
     * @author Oğuzhan Aydın
     */
    public Integer getCarrierRating() {
        return carrierRating;
    }

    /**
     * Sets the rating for the carrier.
     * @param carrierRating The rating value (1-5).
     * @author Oğuzhan Aydın
     */
    public void setCarrierRating(Integer carrierRating) {
        this.carrierRating = carrierRating;
    }

    /**
     * Checks if the order has been rated by the customer.
     * @return True if a valid rating exists, false otherwise.
     * @author Oğuzhan Aydın
     */
    public boolean hasRating() {
        return carrierRating != null && carrierRating >= 1 && carrierRating <= 5;
    }
}

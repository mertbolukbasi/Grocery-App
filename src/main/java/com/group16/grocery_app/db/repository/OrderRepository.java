package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Order;
import com.group16.grocery_app.model.OrderItem;
import com.group16.grocery_app.model.Product;
import com.group16.grocery_app.model.ProductType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations related to orders.
 * Includes saving orders, fetching history, managing status updates, and ratings.
 * @author Oğuzhan Aydın
 */
public class OrderRepository {
    private final Connection connection;

    /**
     * Initializes the repository with a database connection.
     * @author Oğuzhan Aydın
     */
    public OrderRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    /**
     * Saves a new order with the current timestamp as the order date.
     * @param order The order object containing items and total cost.
     * @param customerId The ID of the customer placing the order.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public void saveOrder(Order order, int customerId) throws SQLException {
        saveOrder(order, customerId, null);
    }

    /**
     * Saves a new order to the database with transaction support.
     * Updates product stock levels and inserts order items.
     * @param order The order to be saved.
     * @param customerId The ID of the customer.
     * @param deliveryDate Optional scheduled delivery date.
     * @throws SQLException If saving fails or stock is insufficient.
     * @author Oğuzhan Aydın
     */
    public void saveOrder(Order order, int customerId, java.time.LocalDateTime deliveryDate) throws SQLException {
        try {
            connection.setAutoCommit(false);

            String orderSql;
            PreparedStatement orderStmt;

            if (deliveryDate != null) {
                orderSql = "INSERT INTO OrderInfo (customerID, order_date, status, total_cost, delivery_date) " +
                        "VALUES (?, NOW(), ?, ?, ?)";
                orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, customerId);
                orderStmt.setString(2, "Pending");
                orderStmt.setDouble(3, order.getTotal());
                orderStmt.setTimestamp(4, java.sql.Timestamp.valueOf(deliveryDate));
            } else {
                orderSql = "INSERT INTO OrderInfo (customerID, order_date, status, total_cost) " +
                        "VALUES (?, NOW(), ?, ?)";
                orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, customerId);
                orderStmt.setString(2, "Pending");
                orderStmt.setDouble(3, order.getTotal());
            }

            orderStmt.executeUpdate();

            ResultSet rs = orderStmt.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("Failed to retrieve order ID.");
            }

            int orderId = rs.getInt(1);
            order.setId(orderId);

            String itemSql = "INSERT INTO OrderItems (orderID, productID, amount, unit_price) " +
                    "VALUES (?,?,?,?)";

            String stockSql = "UPDATE ProductInfo SET stock = stock - ? " +
                    "WHERE productID = ? AND stock >= ?";

            try (PreparedStatement itemStmt = connection.prepareStatement(itemSql);
                 PreparedStatement stockStmt = connection.prepareStatement(stockSql)) {

                for (OrderItem item : order.getItems()) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, item.getProduct().getId());
                    itemStmt.setDouble(3, item.getQuantity());
                    itemStmt.setDouble(4, item.getProduct().getEffectivePrice());
                    itemStmt.executeUpdate();

                    stockStmt.setDouble(1, item.getQuantity());
                    stockStmt.setInt(2, item.getProduct().getId());
                    stockStmt.setDouble(3, item.getQuantity());

                    int affected = stockStmt.executeUpdate();
                    if (affected == 0) {
                        throw new SQLException("Insufficient stock for product: " + item.getProduct().getName());
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Retrieves all orders placed by a specific customer.
     * @param customerId The ID of the customer.
     * @return A list of orders sorted by date.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String orderQuery = "SELECT orderID, order_date, delivery_date, status, total_cost, carrier_rating " +
                "FROM OrderInfo WHERE customerID = ? ORDER BY order_date DESC";

        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery)) {
            orderStmt.setInt(1, customerId);
            ResultSet orderRs = orderStmt.executeQuery();

            while (orderRs.next()) {
                int orderId = orderRs.getInt("orderID");
                Timestamp orderDate = orderRs.getTimestamp("order_date");
                Timestamp deliveryDate = orderRs.getTimestamp("delivery_date");
                String status = orderRs.getString("status");
                double total = orderRs.getDouble("total_cost");
                Integer carrierRating = orderRs.getObject("carrier_rating") != null ? orderRs.getInt("carrier_rating") : null;

                LocalDateTime createdAt = orderDate != null ? orderDate.toLocalDateTime() : null;
                LocalDateTime delDate = deliveryDate != null ? deliveryDate.toLocalDateTime() : null;

                Order order = new Order(orderId, new ArrayList<>(), total, createdAt, delDate, status);
                order.setCustomerId(customerId);
                order.setCarrierRating(carrierRating);
                orders.add(order);
            }

            if (!orders.isEmpty()) {
                loadOrderItems(orders);
            }
        }

        return FXCollections.observableArrayList(orders);
    }

    /**
     * Retrieves all orders in the system.
     * @return A list of all orders.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getAllOrders() throws SQLException {
        return getOrdersByStatus(null);
    }

    /**
     * Retrieves orders filtered by their status.
     * @param status The status to filter by (e.g., "Pending").
     * @return A list of matching orders.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByStatus(String status) throws SQLException {
        return getOrdersByStatus(status, false);
    }

    /**
     * Retrieves orders based on status and assignment availability.
     * @param status The status to filter by.
     * @param includeOnlyUnassigned If true, returns only orders not yet assigned to a carrier.
     * @return A list of matching orders.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByStatus(String status, boolean includeOnlyUnassigned) throws SQLException {
        List<Order> orders = new ArrayList<>();

        String orderQuery;
        PreparedStatement orderStmt;

        if (status == null || status.isEmpty()) {
            orderQuery = "SELECT orderID, customerID, carrierID, order_date, delivery_date, status, total_cost, carrier_rating " +
                    "FROM OrderInfo ORDER BY order_date DESC";
            orderStmt = connection.prepareStatement(orderQuery);
        } else {
            if (includeOnlyUnassigned) {
                orderQuery = "SELECT orderID, customerID, carrierID, order_date, delivery_date, status, total_cost, carrier_rating " +
                        "FROM OrderInfo WHERE status = ? AND (carrierID IS NULL OR carrierID = 0) ORDER BY order_date DESC";
            } else {
                orderQuery = "SELECT orderID, customerID, carrierID, order_date, delivery_date, status, total_cost, carrier_rating " +
                        "FROM OrderInfo WHERE status = ? ORDER BY order_date DESC";
            }
            orderStmt = connection.prepareStatement(orderQuery);
            orderStmt.setString(1, status);
        }

        try (ResultSet orderRs = orderStmt.executeQuery()) {
            while (orderRs.next()) {
                int orderId = orderRs.getInt("orderID");
                int customerId = orderRs.getInt("customerID");
                Integer carrierId = orderRs.getObject("carrierID") != null ? orderRs.getInt("carrierID") : null;
                Timestamp orderDate = orderRs.getTimestamp("order_date");
                Timestamp deliveryDate = orderRs.getTimestamp("delivery_date");
                String orderStatus = orderRs.getString("status");
                double total = orderRs.getDouble("total_cost");
                Integer carrierRating = orderRs.getObject("carrier_rating") != null ? orderRs.getInt("carrier_rating") : null;

                LocalDateTime createdAt = orderDate != null ? orderDate.toLocalDateTime() : null;
                LocalDateTime delDate = deliveryDate != null ? deliveryDate.toLocalDateTime() : null;

                Order order = new Order(orderId, new ArrayList<>(), total, createdAt, delDate, orderStatus);
                order.setCustomerId(customerId);
                order.setCarrierId(carrierId);
                order.setCarrierRating(carrierRating);
                orders.add(order);
            }

            if (!orders.isEmpty()) {
                loadOrderItems(orders);
            }
        }

        return FXCollections.observableArrayList(orders);
    }

    /**
     * Retrieves orders assigned to a specific carrier.
     * @param carrierId The ID of the carrier.
     * @param status Optional status filter (e.g., "Delivered").
     * @return A list of orders assigned to the carrier.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public ObservableList<Order> getOrdersByCarrierId(int carrierId, String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String orderQuery = "SELECT orderID, customerID, carrierID, order_date, delivery_date, status, total_cost, carrier_rating " +
                "FROM OrderInfo WHERE carrierID = ?" +
                (status != null ? " AND status = ?" : "") +
                " ORDER BY order_date DESC";

        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery)) {
            orderStmt.setInt(1, carrierId);
            if (status != null) {
                orderStmt.setString(2, status);
            }
            ResultSet orderRs = orderStmt.executeQuery();

            while (orderRs.next()) {
                int orderId = orderRs.getInt("orderID");
                int customerId = orderRs.getInt("customerID");
                Integer orderCarrierId = orderRs.getObject("carrierID") != null ? orderRs.getInt("carrierID") : null;
                Timestamp orderDate = orderRs.getTimestamp("order_date");
                Timestamp deliveryDate = orderRs.getTimestamp("delivery_date");
                String orderStatus = orderRs.getString("status");
                double total = orderRs.getDouble("total_cost");
                Integer carrierRating = orderRs.getObject("carrier_rating") != null ? orderRs.getInt("carrier_rating") : null;

                LocalDateTime createdAt = orderDate != null ? orderDate.toLocalDateTime() : null;
                LocalDateTime delDate = deliveryDate != null ? deliveryDate.toLocalDateTime() : null;

                Order order = new Order(orderId, new ArrayList<>(), total, createdAt, delDate, orderStatus);
                order.setCustomerId(customerId);
                order.setCarrierId(orderCarrierId);
                order.setCarrierRating(carrierRating);
                orders.add(order);
            }

            if (!orders.isEmpty()) {
                loadOrderItems(orders);
            }
        }

        return FXCollections.observableArrayList(orders);
    }

    /**
     * Loads product details for a list of orders.
     * Populates the order objects with their respective items.
     * @param orders The list of orders to populate.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    private void loadOrderItems(List<Order> orders) throws SQLException {
        if (orders.isEmpty()) return;

        List<Integer> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }

        String placeholders = "";
        for (int i = 0; i < orderIds.size(); i++) {
            if (i > 0) placeholders += ",";
            placeholders += "?";
        }

        String itemsQuery = "SELECT oi.orderID, oi.productID, oi.amount, oi.unit_price, " +
                "p.name, p.type, p.price, p.stock, p.threshold, p.image_data " +
                "FROM OrderItems oi " +
                "JOIN ProductInfo p ON oi.productID = p.productID " +
                "WHERE oi.orderID IN (" + placeholders + ")";

        try (PreparedStatement itemsStmt = connection.prepareStatement(itemsQuery)) {
            for (int i = 0; i < orderIds.size(); i++) {
                itemsStmt.setInt(i + 1, orderIds.get(i));
            }

            try (ResultSet itemsRs = itemsStmt.executeQuery()) {
                while (itemsRs.next()) {
                    int orderId = itemsRs.getInt("orderID");
                    Order order = null;
                    for (Order o : orders) {
                        if (o.getId() == orderId) {
                            order = o;
                            break;
                        }
                    }

                    if (order != null) {
                        int productId = itemsRs.getInt("productID");
                        double quantity = itemsRs.getDouble("amount");
                        double unitPrice = itemsRs.getDouble("unit_price");

                        String productName = itemsRs.getString("name");
                        ProductType productType = ProductType.valueOf(itemsRs.getString("type").toUpperCase());
                        double productPrice = itemsRs.getDouble("price");
                        double productStock = itemsRs.getDouble("stock");
                        double productThreshold = itemsRs.getDouble("threshold");

                        Blob imageBlob = itemsRs.getBlob("image_data");
                        Image productImage = null;
                        if (imageBlob != null) {
                            InputStream inputStream = imageBlob.getBinaryStream();
                            productImage = new Image(inputStream);
                        }

                        Product product = new Product(productId, productName, productType, productPrice,
                                productStock, productThreshold, productImage);

                        OrderItem orderItem = new OrderItem(product, quantity, unitPrice);
                        order.getItems().add(orderItem);
                    }
                }
            }
        }
    }

    /**
     * Assigns a pending order to a carrier.
     * @param orderId The ID of the order.
     * @param carrierId The ID of the carrier accepting the order.
     * @return True if successful, false otherwise.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public boolean selectOrder(int orderId, int carrierId) throws SQLException {
        String query = "UPDATE OrderInfo SET carrierID = ?, status = 'Selected' WHERE orderID = ? AND status = 'Pending'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carrierId);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Unassigns an order from a carrier, making it pending again.
     * @param orderId The ID of the order.
     * @param carrierId The ID of the carrier currently assigned.
     * @return True if successful, false otherwise.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public boolean unselectOrder(int orderId, int carrierId) throws SQLException {
        String query = "UPDATE OrderInfo SET carrierID = NULL, status = 'Pending' WHERE orderID = ? AND carrierID = ? AND status = 'Selected'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, carrierId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Marks an order as delivered and updates the delivery time.
     * @param orderId The ID of the order.
     * @param deliveryDateTime The actual delivery time.
     * @return True if successful, false otherwise.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public boolean completeDelivery(int orderId, LocalDateTime deliveryDateTime) throws SQLException {
        String query = "UPDATE OrderInfo SET status = 'Delivered', delivery_date = ? WHERE orderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, java.sql.Timestamp.valueOf(deliveryDateTime));
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Updates the carrier rating for a completed order.
     * @param orderId The ID of the order.
     * @param rating The rating value (1-5).
     * @return True if successful, false otherwise.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public boolean rateCarrier(int orderId, int rating) throws SQLException {
        if (rating < 1 || rating > 5) {
            return false;
        }
        String query = "UPDATE OrderInfo SET carrier_rating = ? WHERE orderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, rating);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Saves the generated invoice text to the database.
     * @param orderId The ID of the order.
     * @param invoiceText The content of the invoice.
     * @return True if successful.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public boolean saveInvoice(int orderId, String invoiceText) throws SQLException {
        String query = "UPDATE OrderInfo SET invoice_data = ? WHERE orderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, invoiceText);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Retrieves the stored invoice data for an order.
     * @param orderId The ID of the order.
     * @return The invoice text, or null if not found.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public String getInvoice(int orderId) throws SQLException {
        String query = "SELECT invoice_data FROM OrderInfo WHERE orderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("invoice_data");
            }
        }
        return null;
    }

    /**
     * Counts the total number of completed orders for a customer.
     * @param customerId The ID of the customer.
     * @return The count of delivered orders.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public int getCompletedOrdersCount(int customerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM OrderInfo WHERE customerID = ? AND status = 'Delivered'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Calculates the average rating for a carrier.
     * @param carrierId The ID of the carrier.
     * @return The average rating, or -1.0 if no ratings exist.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public double getAverageCarrierRating(int carrierId) throws SQLException {
        String query = "SELECT AVG(carrier_rating) FROM OrderInfo WHERE carrierID = ? AND carrier_rating IS NOT NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carrierId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double avg = rs.getDouble(1);

                if (rs.wasNull()) {
                    return -1.0;
                }
                return avg;
            }
        }
        return -1.0;
    }

    /**
     * Cancels a pending order for a customer.
     * @param orderId The ID of the order.
     * @param customerId The ID of the customer requesting cancellation.
     * @return True if successful, false if order is not pending.
     * @throws SQLException If a database error occurs.
     * @author Oğuzhan Aydın
     */
    public boolean cancelOrder(int orderId, int customerId) throws SQLException {
        String query = "UPDATE OrderInfo SET status = 'Cancelled' WHERE orderID = ? AND customerID = ? AND status = 'Pending'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, customerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
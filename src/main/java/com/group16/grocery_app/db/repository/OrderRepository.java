package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public List<Order> findPendingOrders() {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT o.orderID, o.total_cost, o.status, o.delivery_date, " +
                "u.first_name, u.last_name, u.address " +
                "FROM OrderInfo o " +
                "JOIN UserInfo u ON o.customerID = u.userID " +
                "WHERE o.status = 'Pending'";

        Connection conn = Database.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs, conn);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending orders: " + e.getMessage());
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> findOrdersByCarrier(int carrierId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.orderID, o.total_cost, o.status, o.delivery_date, " +
                "u.first_name, u.last_name, u.address " +
                "FROM OrderInfo o " +
                "JOIN UserInfo u ON o.customerID = u.userID " +
                "WHERE o.carrierID = ?";

        Connection conn = Database.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carrierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs, conn);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrderToCarrier(int orderId, int carrierId) {
        String sql = "UPDATE OrderInfo SET carrierID = ?, status = 'Selected' " +
                "WHERE orderID = ? AND status = 'Pending'";

        Connection conn = Database.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, carrierId);
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completeOrder(int orderId) {
        String sql = "UPDATE OrderInfo SET status = 'Delivered', delivery_date = NOW() " +
                "WHERE orderID = ?";

        Connection conn = Database.getInstance().getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Order mapResultSetToOrder(ResultSet rs, Connection conn) throws SQLException {
        int id = rs.getInt("orderID");
        double cost = rs.getDouble("total_cost");
        String status = rs.getString("status");

        Timestamp ts = rs.getTimestamp("delivery_date");
        LocalDateTime date = (ts != null) ? ts.toLocalDateTime() : null;

        String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
        String address = rs.getString("address");

        String products = getProductStringForOrder(id, conn);

        return new Order(id, products, fullName, address, cost, date, status);
    }

    private String getProductStringForOrder(int orderId, Connection conn) {
        StringBuilder sb = new StringBuilder();

        String sql = "SELECT p.product_name, oi.quantity " +
                "FROM OrderItems oi " +
                "JOIN ProductInfo p ON oi.productID = p.productID " +
                "WHERE oi.orderID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("product_name");
                    int qty = rs.getInt("quantity");

                    if (sb.length() > 0) sb.append(", ");
                    sb.append(name).append(" x").append(qty);
                }
            }
        } catch (SQLException e) {
            return "Product info unavailable";
        }

        if (sb.length() == 0) return "No Products";
        return sb.toString();
    }
}
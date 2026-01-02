package com.group16.grocery_app.db.repository;

import com.group16.grocery_app.db.Database;
import com.group16.grocery_app.model.Product;
import com.group16.grocery_app.model.ProductType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.sql.*;

public class ProductRepository {

    private Connection connection;

    public ProductRepository() {
        this.connection = Database.getInstance().getConnection();
    }

    public ObservableList<Product> findAll() throws SQLException {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String query = "SELECT * FROM ProductInfo ORDER BY name ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Blob blob = rs.getBlob("image_data");
                Image image = null;
                if (blob != null) {
                    InputStream inputStream = blob.getBinaryStream();
                    image = new Image(inputStream);
                }

                products.add(new Product(
                        rs.getInt("productID"),
                        rs.getString("name"),
                        ProductType.valueOf(rs.getString("type").toUpperCase()),
                        rs.getDouble("price"),
                        rs.getDouble("stock"),
                        rs.getDouble("threshold"),
                        image
                ));
            }
        } catch (SQLException e) {
            throw new SQLException("Product could not find.", e.getMessage());
        }
        return products;
    }

    public boolean addProduct(String name, ProductType type, double price, double stock, double threshold) throws SQLException {
        String query = "INSERT INTO ProductInfo (name, type, price, stock, threshold) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, type.toString().toLowerCase());
            stmt.setDouble(3, price);
            stmt.setDouble(4, stock);
            stmt.setDouble(5, threshold);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateProduct(int productId, String name, ProductType type, double price, double stock, double threshold) throws SQLException {
        String query = "UPDATE ProductInfo SET name = ?, type = ?, price = ?, stock = ?, threshold = ? WHERE productID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, type.toString().toLowerCase());
            stmt.setDouble(3, price);
            stmt.setDouble(4, stock);
            stmt.setDouble(5, threshold);
            stmt.setInt(6, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean removeProduct(int productId) throws SQLException {
        String query = "DELETE FROM ProductInfo WHERE productID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

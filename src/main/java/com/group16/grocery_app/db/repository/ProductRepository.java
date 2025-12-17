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
                        rs.getObject("type", ProductType.class),
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
}

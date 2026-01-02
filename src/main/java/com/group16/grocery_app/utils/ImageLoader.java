package com.group16.grocery_app.utils;

import com.group16.grocery_app.db.Database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility to populate the database with product images found in the resources folder.
 *
 * @author Mert Bölükbaşı
 */
public class ImageLoader {

    /**
     * Scans the database for products missing images and tries to update them
     * by matching filenames in the resource path.
     *
     * @author Mert Bölükbaşı
     */
    public static void loadImagesToDatabase() {
        Connection connection = Database.getInstance().getConnection();
        if (connection == null) {
            System.err.println("Cannot load images: Database connection is null.");
            return;
        }

        String selectQuery = "SELECT productID, name FROM ProductInfo WHERE image_data IS NULL";
        String updateQuery = "UPDATE ProductInfo SET image_data = ? WHERE productID = ?";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
             ResultSet rs = selectStmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("productID");
                String name = rs.getString("name");

                InputStream is = findImageStream(name);

                if (is != null) {
                    try {
                        byte[] imageBytes = is.readAllBytes();
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setBytes(1, imageBytes);
                            updateStmt.setInt(2, id);
                            updateStmt.executeUpdate();
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading image for " + name + ": " + e.getMessage());
                    } finally {
                        try {
                            is.close();
                        } catch (IOException e) {

                        }
                    }
                } else {
                    System.out.println("Image file not found for: " + name);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error during image loading: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper to locate an image file for a given product name.
     * It tries various extensions (jpg, png, etc.) and casing formats to find a match.
     *
     * @param name The product name to search for.
     * @return An InputStream of the image, or null if not found.
     */
    private static InputStream findImageStream(String name) {
        String[] extensions = {".jpg", ".png", ".jpeg", ".JPG"};
        Class<?> clazz = ImageLoader.class;

        for (String ext : extensions) {
            InputStream is = clazz.getResourceAsStream("/" + name + ext);
            if (is != null) return is;
        }

        String lower = name.toLowerCase();
        for (String ext : extensions) {
            InputStream is = clazz.getResourceAsStream("/" + lower + ext);
            if (is != null) return is;
        }

        if (name.length() > 1) {
            String cap = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            for (String ext : extensions) {
                InputStream is = clazz.getResourceAsStream("/" + cap + ext);
                if (is != null) return is;
            }
        }

        return null;
    }
}
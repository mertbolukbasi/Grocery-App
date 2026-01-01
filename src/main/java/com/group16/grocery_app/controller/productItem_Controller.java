package com.group16.grocery_app.controller;

import com.group16.grocery_app.model.Cart;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import com.group16.grocery_app.model.Product;
import javafx.scene.image.ImageView;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

public class productItem_Controller {

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private MFXTextField quantityField;

    @FXML
    private MFXButton addToCartButton;

    @FXML
    private ImageView productImageView;

    private Product product;

    public void setProduct(Product product) {
        this.product = product;
        if (nameLabel != null) {
            nameLabel.setText(product.getName());
        }
        updatePriceDisplay();
        if (product.getImage() != null && productImageView != null) {
            productImageView.setImage(product.getImage());
        }
    }

    private void updatePriceDisplay() {
        if (product == null) return;

        double cartQuantity = (cart != null) ? cart.getQuantityOfProduct(product) : 0.0;
        double effectivePrice = product.getEffectivePrice(cartQuantity);
        priceLabel.setText("₺" + String.format("%.2f", effectivePrice));
    }

    private Cart cart;

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @FXML
    private void handleAddToCart() {

        double quantity;

        try {
            quantity = Double.parseDouble(quantityField.getText());

            if (quantity <= 0) {
                throw new NumberFormatException();
            }

        } catch (Exception e) {
            showInfo("Invalid quantity", "Please enter a valid kg value.");
            return;
        }

        double alreadyInCart = cart.getQuantityOfProduct(product);
        double totalRequested = alreadyInCart + quantity;

        if (totalRequested > product.getStock()) {
            showInfo("Insufficient stock", "Available stock: " + product.getStock() + " kg\n" + "Already in cart: " + alreadyInCart + " kg");
            return;
        }

        cart.addProduct(product, quantity);

        updatePriceDisplay();

        showInfo("Added to cart", product.getName() + " (" + quantity + " kg) added to cart.");

        quantityField.clear();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(getClass().getResource("/app.css").toExternalForm());

        alert.showAndWait();
    }

}

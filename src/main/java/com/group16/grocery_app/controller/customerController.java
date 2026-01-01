package com.group16.grocery_app.controller;

import com.group16.grocery_app.model.Cart;
import com.group16.grocery_app.model.User;
import com.group16.grocery_app.utils.CartManager;
import com.group16.grocery_app.db.service.MessageService;
import com.group16.grocery_app.model.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.github.palexdev.materialfx.controls.MFXTextField;
import com.group16.grocery_app.model.Product;
import com.group16.grocery_app.db.service.ProductService;
import com.group16.grocery_app.db.service.UserService;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class customerController {

    private Cart cart;
    private final ProductService productService = new ProductService();
    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();
    private User currentUser;
    private final CartManager cartManager = CartManager.getInstance();

    @FXML
    private Label usernameLabel;

    @FXML
    private Accordion productAccordion;

    @FXML
    private MFXTextField searchField;

    private ObservableList<Product> allProducts;

    @FXML
    public void initialize() {
        try {
            allProducts = productService.getAllProducts();
            System.out.println("Loaded " + (allProducts != null ? allProducts.size() : 0) + " products");
            if (allProducts != null && !allProducts.isEmpty()) {
                showProducts(allProducts);
            } else {
                System.out.println("Warning: No products loaded from database");
            }
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldText, newText) -> {filterProducts(newText);});
            }
        } catch (Exception e) {
            System.err.println("Error in initialize(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    private void clearAccordion() {
        for (TitledPane pane : productAccordion.getPanes()) {
            VBox box = (VBox) pane.getContent();
            if (box != null) {
                box.getChildren().clear();
            }
        }
    }

    private void filterProducts(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            showProducts(allProducts);
            return;
        }

        String lower = keyword.toLowerCase();

        ObservableList<Product> filtered = allProducts.filtered(
                p -> p.getName().toLowerCase().contains(lower)
        );

        showProducts(filtered);
    }

    private void showProducts(ObservableList<Product> products) {
        try {
            if (productAccordion == null) {
                System.err.println("Error: productAccordion is null");
                return;
            }

            clearAccordion();
            System.out.println("Showing " + products.size() + " products");

            int addedCount = 0;
            for (Product product : products) {
                if (product.getStock() <= 0) {
                    System.out.println("Skipping " + product.getName() + " - stock is 0");
                    continue;
                }

                addProductToAccordion(product);
                addedCount++;
            }
            System.out.println("Added " + addedCount + " products to accordion");
        } catch (Exception e) {
            System.err.println("Error in showProducts(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addProductToAccordion(Product product) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/productItem.fxml")
            );

            HBox productItem = loader.load();

            productItem_Controller controller = loader.getController();
            controller.setProduct(product);
            controller.setCart(cart);

            String productTypeName = product.getType().name();
            boolean found = false;

            for (TitledPane pane : productAccordion.getPanes()) {
                String paneText = pane.getText().toUpperCase();

                // Match "Vegetables" with "VEGETABLE" and "Fruits" with "FRUIT"
                if ((paneText.equals("VEGETABLES") && productTypeName.equals("VEGETABLE")) ||
                        (paneText.equals("FRUITS") && productTypeName.equals("FRUIT"))) {

                    VBox box = (VBox) pane.getContent();

                    if (box != null) {
                        box.getChildren().add(productItem);
                        pane.setExpanded(true);
                        found = true;
                        System.out.println("Added " + product.getName() + " (" + productTypeName + ") to " + pane.getText());
                        break;
                    } else {
                        System.err.println("Error: VBox content is null for pane: " + pane.getText());
                    }
                }
            }

            if (!found) {
                System.err.println("Warning: Could not find matching pane for product: " + product.getName() + " type: " + productTypeName);
            }
        } catch (Exception e) {
            System.err.println("Error adding product " + product.getName() + " to accordion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Get or create cart for this user from CartManager to persist across navigation
        if (user != null) {
            this.cart = cartManager.getCart(user.getId());
        }
        // Reload products to ensure they're displayed (in case initialize() was called before FXML was ready)
        if (productAccordion != null) {
            allProducts = productService.getAllProducts();
            showProducts(allProducts);
        }
    }

    @FXML
    private void handleOpenCart() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/cart.fxml")
            );

            Scene scene = new Scene(loader.load());

            CartController controller = loader.getController();
            controller.setCart(cart);
            if (currentUser != null) {
                controller.setCurrentUser(currentUser);
            }

            Stage stage = new Stage();
            stage.setTitle("Cart");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Confirm Logout");
        confirm.setContentText("Are you sure you want to logout?");
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/appStyles.css").toExternalForm()
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Clear cart on logout
                    if (currentUser != null) {
                        cartManager.clearCart(currentUser.getId());
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group16/grocery_app/LoginView.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) usernameLabel.getScene().getWindow();
                    Scene scene = new Scene(root, 960, 540);
                    stage.setScene(scene);
                    stage.setTitle("Group16 GreenGrocer");
                    stage.centerOnScreen();
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to logout.");
                }
            }
        });
    }

    @FXML
    private void handleMyOrders() {
        try {
            if (currentUser == null) {
                showAlert(Alert.AlertType.WARNING, "Error", "User information is missing.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/orders.fxml"));
            Parent root = loader.load();
            OrdersController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Group16 GreenGrocer - My Orders");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load orders.");
        }
    }

    @FXML
    private void handleMessages() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "User information is missing.");
            return;
        }

        // Find owner user
        User owner = userService.findOwner();
        if (owner == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Owner not found. Cannot send messages.");
            return;
        }

        // Create message dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Messages - Contact Owner");
        dialog.setHeaderText("Send a message to the store owner");

        // Create message display area
        ScrollPane scrollPane = new ScrollPane();
        VBox messagesBox = new VBox(10);
        messagesBox.setStyle("-fx-padding: 10px;");
        scrollPane.setContent(messagesBox);
        scrollPane.setPrefHeight(300);
        scrollPane.setFitToWidth(true);

        // Load existing messages
        ObservableList<Message> messages = messageService.getMessagesBetween(currentUser.getId(), owner.getId());
        for (Message msg : messages) {
            Label msgLabel = new Label(msg.getContent());
            msgLabel.setWrapText(true);
            msgLabel.setMaxWidth(400);
            msgLabel.setPadding(new javafx.geometry.Insets(8));

            if (msg.getSenderId() == currentUser.getId()) {
                msgLabel.setStyle("-fx-background-color: #FFE5E0; -fx-background-radius: 10px; -fx-padding: 8px;");
                msgLabel.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            } else {
                msgLabel.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 10px; -fx-padding: 8px;");
                msgLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            }

            messagesBox.getChildren().add(msgLabel);
        }

        // Message input
        TextArea messageInput = new TextArea();
        messageInput.setPromptText("Type your message here...");
        messageInput.setPrefRowCount(3);
        messageInput.setWrapText(true);

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Conversation with Owner:"),
                scrollPane,
                new Label("Your Message:"),
                messageInput
        );
        content.setPadding(new javafx.geometry.Insets(20));
        content.setPrefWidth(500);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setText("Send");
            // Prevent dialog from closing on validation errors
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                String message = messageInput.getText().trim();
                if (message.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Empty Message", "Please enter a message.");
                    event.consume(); // Prevent dialog from closing
                    return;
                }

                boolean success = messageService.sendMessage(currentUser.getId(), owner.getId(), message);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Message sent successfully!");
                    // Don't consume event - let dialog close on success
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to send message.");
                    event.consume(); // Prevent dialog from closing
                }
            });
        }

        dialog.setResultConverter(button -> button == ButtonType.OK ? null : null);

        dialog.showAndWait();
    }

    @FXML
    private void handleEditProfile() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "User information is missing.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Profile");
        dialog.setHeaderText("Update your profile information");

        TextField addressField = new TextField(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        TextField phoneField = new TextField(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        phoneField.setPromptText("05332100598");

        // Add helper label for phone number format
        Label phoneHelpLabel = new Label("Format: Must start with 0 and be exactly 11 digits (e.g., 05332100598)");
        phoneHelpLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Address:"), addressField,
                new Label("Phone Number:"), phoneField,
                phoneHelpLabel
        );
        content.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Get the OK button and initially disable validation
        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        // Validate phone number format
        phoneField.textProperty().addListener((obs, oldText, newText) -> {
            if (okButton != null) {
                String phone = newText != null ? newText.trim() : "";
                boolean isValid = validatePhoneNumber(phone);
                okButton.setDisable(!isValid);
            }
        });

        // Initial validation
        if (okButton != null) {
            String currentPhone = phoneField.getText() != null ? phoneField.getText().trim() : "";
            okButton.setDisable(!validatePhoneNumber(currentPhone));
        }

        // Prevent dialog from closing on validation errors
        if (okButton != null) {
            okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                try {
                    String newAddress = addressField.getText().trim();
                    String newPhone = phoneField.getText().trim();

                    // Validate phone number format
                    if (!validatePhoneNumber(newPhone)) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Phone Number",
                                "Phone number must start with 0 and be exactly 11 digits.\n" +
                                        "Example format: 05332100598");
                        event.consume(); // Prevent dialog from closing
                        return;
                    }

                    // Check if phone number is already used by another user
                    com.group16.grocery_app.db.service.UserService userService =
                            new com.group16.grocery_app.db.service.UserService();
                    if (!newPhone.isEmpty() && userService.phoneNumberExists(newPhone, currentUser.getId())) {
                        showAlert(Alert.AlertType.ERROR, "Phone Number Already Exists",
                                "This phone number is already registered to another user. Please use a different phone number.");
                        event.consume(); // Prevent dialog from closing
                        return;
                    }

                    // Update in database
                    boolean success = userService.updateProfile(currentUser.getId(), newAddress, newPhone);

                    if (success) {
                        // Update local user object
                        currentUser.setAddress(newAddress);
                        currentUser.setPhoneNumber(newPhone);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
                        // Don't consume event - let dialog close on success
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile. Phone number may already be in use.");
                        event.consume(); // Prevent dialog from closing
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating profile.");
                    e.printStackTrace();
                    event.consume(); // Prevent dialog from closing
                }
            });
        }

        dialog.setResultConverter(button -> button == ButtonType.OK ? null : null);

        dialog.showAndWait();
    }

    /**
     * Validates phone number format: must start with 0 and be exactly 11 digits
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    private boolean validatePhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Allow empty phone number (optional field)
        }

        String trimmed = phone.trim();

        // Must start with 0
        if (!trimmed.startsWith("0")) {
            return false;
        }

        // Must be exactly 11 digits
        if (trimmed.length() != 11) {
            return false;
        }

        // Must contain only digits
        return trimmed.matches("\\d+");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/app.css").toExternalForm()
        );
        alert.showAndWait();
    }
}

package com.group16.grocery_app.controller;

import com.group16.grocery_app.db.service.OrderService;
import com.group16.grocery_app.model.Order;
import com.group16.grocery_app.model.OrderItem;
import com.group16.grocery_app.model.User;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for managing the customer's order history.
 * Handles viewing past orders, order details, invoices, and cancellations.
 * @author Oğuzhan Aydın
 */
public class OrdersController {

    private User currentUser;
    private OrderService orderService;
    private com.group16.grocery_app.db.service.UserService userService =
            new com.group16.grocery_app.db.service.UserService();

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> orderIdCol;

    @FXML
    private TableColumn<Order, String> dateCol;

    @FXML
    private TableColumn<Order, String> deliveryCol;

    @FXML
    private TableColumn<Order, String> statusCol;

    @FXML
    private TableColumn<Order, Double> totalCol;

    @FXML
    private TableColumn<Order, Void> ratingCol;

    @FXML
    private TableView<OrderItem> itemsTable;

    @FXML
    private TableColumn<OrderItem, String> itemNameCol;

    @FXML
    private TableColumn<OrderItem, Double> itemQuantityCol;

    @FXML
    private TableColumn<OrderItem, Double> itemPriceCol;

    @FXML
    private TableColumn<OrderItem, Double> itemTotalCol;

    @FXML
    private VBox orderDetailsBox;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Sets the current user and loads their order history.
     * @param user The logged-in customer.
     * @author Oğuzhan Aydın
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.orderService = new OrderService();
        loadOrders();
    }

    /**
     * Initializes the controller, sets up tables and selection listeners.
     * @author Oğuzhan Aydın
     */
    @FXML
    public void initialize() {
        setupOrdersTable();
        setupItemsTable();

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldOrder, newOrder) -> {
            if (newOrder != null) {
                showOrderDetails(newOrder);
            } else {
                orderDetailsBox.setVisible(false);
            }
        });
    }

    /**
     * Configures the main orders table columns and the rating button.
     * @author Oğuzhan Aydın
     */
    private void setupOrdersTable() {
        orderIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        dateCol.setCellValueFactory(data -> {
            LocalDateTime date = data.getValue().getCreatedAt();
            String dateStr = date != null ? date.format(dateFormatter) : "N/A";
            return new SimpleStringProperty(dateStr);
        });
        deliveryCol.setCellValueFactory(data -> {
            LocalDateTime date = data.getValue().getDeliveryDate();
            String dateStr = date != null ? date.format(dateFormatter) : "Not set";
            return new SimpleStringProperty(dateStr);
        });
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        totalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());

        totalCol.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("₺%.2f", item));
                }
            }
        });

        if (ratingCol != null) {
            ratingCol.setCellFactory(column -> new TableCell<Order, Void>() {
                private final Button rateButton = new Button("Rate");
                private final Label ratingLabel = new Label();

                {
                    rateButton.setStyle("-fx-background-color: #FF4B2B; -fx-text-fill: white; -fx-padding: 5px 15px; -fx-border-radius: 5px;");
                    rateButton.setOnAction(event -> {
                        Order order = getTableView().getItems().get(getIndex());
                        showRatingDialog(order);
                    });
                    ratingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #FFA500;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Order order = getTableView().getItems().get(getIndex());
                        if (order.hasRating() && order.getCarrierRating() != null) {
                            ratingLabel.setText(order.getCarrierRating() + " ⭐");
                            setGraphic(ratingLabel);
                            setText(null);
                        } else if ("Delivered".equals(order.getStatus()) && !order.hasRating()) {
                            setGraphic(rateButton);
                            setText(null);
                        } else {
                            setGraphic(null);
                            setText("N/A");
                        }
                    }
                }
            });
        }
    }

    /**
     * Displays a dialog to allow the customer to rate the carrier.
     * @param order The order to be rated.
     * @author Oğuzhan Aydın
     */
    private void showRatingDialog(Order order) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Rate Carrier");
        dialog.setHeaderText("How would you rate the delivery service?");

        HBox ratingBox = new HBox(10);
        ratingBox.setAlignment(javafx.geometry.Pos.CENTER);
        ratingBox.setPadding(new javafx.geometry.Insets(20));

        Button[] stars = new Button[5];
        final int[] selectedRating = {0};

        for (int i = 0; i < 5; i++) {
            final int rating = i + 1;
            Button star = new Button("★");
            star.setStyle("-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: #CCCCCC;");
            star.setOnAction(e -> {
                selectedRating[0] = rating;
                for (int j = 0; j < 5; j++) {
                    if (j < rating) {
                        stars[j].setStyle("-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: #FFD700;");
                    } else {
                        stars[j].setStyle("-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: #CCCCCC;");
                    }
                }
            });
            stars[i] = star;
            ratingBox.getChildren().add(star);
        }

        dialog.getDialogPane().setContent(ratingBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK && selectedRating[0] > 0) {
                return selectedRating[0];
            }
            return null;
        });

        dialog.showAndWait().ifPresent(rating -> {
            OrderService orderService = new OrderService();
            boolean success = orderService.rateCarrier(order.getId(), rating);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Thank you for your rating!");
                loadOrders();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit rating.");
            }
        });
    }

    /**
     * Configures the columns for the order items details table.
     * @author Oğuzhan Aydın
     */
    private void setupItemsTable() {
        itemNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));
        itemQuantityCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getQuantity()).asObject());
        itemPriceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getUnitPrice()).asObject());
        itemTotalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());

        itemPriceCol.setCellFactory(column -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("₺%.2f", item));
                }
            }
        });

        itemTotalCol.setCellFactory(column -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("₺%.2f", item));
                }
            }
        });

        itemQuantityCol.setCellFactory(column -> new TableCell<OrderItem, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f kg", item));
                }
            }
        });
    }

    /**
     * Loads all orders for the current customer from the database.
     * @author Oğuzhan Aydın
     */
    private void loadOrders() {
        if (currentUser == null) {
            return;
        }

        ObservableList<Order> orders = orderService.getOrdersByCustomerId(currentUser.getId());
        ordersTable.setItems(orders);
    }

    /**
     * Shows the items of the selected order in the details panel.
     * @param order The selected order.
     * @author Oğuzhan Aydın
     */
    private void showOrderDetails(Order order) {
        orderDetailsBox.setVisible(true);
        ObservableList<OrderItem> items = FXCollections.observableArrayList(order.getItems());
        itemsTable.setItems(items);
    }

    /**
     * Generates and opens a PDF invoice for the selected order.
     * @author Oğuzhan Aydın
     */
    @FXML
    private void handleViewInvoice() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to view invoice.");
            return;
        }

        try {
            User customer = userService.findById(selected.getCustomerId());
            String customerName = customer != null ?
                    (customer.getFirstName() + " " + customer.getLastName()) : "Unknown Customer";
            String customerAddress = customer != null ? customer.getAddress() : "";

            byte[] pdfBytes = com.group16.grocery_app.utils.InvoiceGenerator.generateInvoicePDF(
                    selected, customerName, customerAddress);

            java.io.File tempFile = java.io.File.createTempFile("invoice_" + selected.getId() + "_", ".pdf");
            tempFile.deleteOnExit();
            java.nio.file.Files.write(tempFile.toPath(), pdfBytes);

            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(tempFile);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "PDF Generated",
                        "PDF invoice saved to: " + tempFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF invoice: " + e.getMessage());
        }
    }

    /**
     * Cancels the selected order if its status is still Pending.
     * @author Oğuzhan Aydın
     */
    @FXML
    private void handleCancelOrder() {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to cancel.");
            return;
        }

        if (!"Pending".equals(selected.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Cannot Cancel", "Only pending orders can be cancelled.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Order");
        confirm.setHeaderText("Cancel Order #" + selected.getId());
        confirm.setContentText("Are you sure you want to cancel this order?");
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/appStyles.css").toExternalForm()
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = orderService.cancelOrder(selected.getId(), currentUser.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Order cancelled successfully.");
                    loadOrders();
                    orderDetailsBox.setVisible(false);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel order. It may have been processed already.");
                }
            }
        });
    }

    /**
     * Returns the user to the main customer dashboard.
     * @author Oğuzhan Aydın
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/customer.fxml"));
            Parent root = loader.load();
            CustomerController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            controller.setUsername(currentUser.getUsername());

            Stage stage = (Stage) ordersTable.getScene().getWindow();
            Scene scene = new Scene(root, 960, 540);
            stage.setScene(scene);
            stage.setTitle("Group16 GreenGrocer");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to go back to customer view.");
        }
    }

    /**
     * Displays an alert dialog with the given type, title, and message.
     * @param type The type of the alert (e.g., Error, Information).
     * @param title The title of the alert window.
     * @param message The content message.
     * @author Oğuzhan Aydın
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/appStyles.css").toExternalForm()
        );
        alert.showAndWait();
    }
}


package com.group16.grocery_app.controller;

import com.group16.grocery_app.model.*;
import com.group16.grocery_app.db.service.OrderService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for carrier interface managing order selection and delivery.
 * @author Oğuzhan Aydın
 */
public class CarrierController {

    private User currentUser;
    private OrderService orderService = new OrderService();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private Label carrierUsernameLabel;
    @FXML private TableView<Order> availableOrdersTable;
    @FXML private TableColumn<Order, Integer> availOrderIdCol;
    @FXML private TableColumn<Order, Double> availTotalCol;
    @FXML private TableColumn<Order, String> availDateCol;

    @FXML private TableView<Order> selectedOrdersTable;
    @FXML private TableColumn<Order, Integer> selOrderIdCol;
    @FXML private TableColumn<Order, String> selCustomerCol;
    @FXML private TableColumn<Order, String> selAddressCol;

    @FXML private TableView<Order> completedOrdersTable;
    @FXML private TableColumn<Order, Integer> compOrderIdCol;
    @FXML private TableColumn<Order, String> compDateCol;
    @FXML private TableColumn<Order, Double> compRatingCol;

    @FXML private VBox orderDetailsBox;
    @FXML private Label orderDetailsLabel;

    /**
     * Sets the current logged-in carrier user.
     * @param user the carrier user
     * @author Oğuzhan Aydın
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (carrierUsernameLabel != null) {
            carrierUsernameLabel.setText(user.getUsername());
        }
        initialize();
    }

    /**
     * Initializes the controller and sets up tables.
     * @author Oğuzhan Aydın
     */
    @FXML
    public void initialize() {
        if (currentUser == null) return;

        setupAvailableOrdersTable();
        setupSelectedOrdersTable();
        setupCompletedOrdersTable();
        loadOrders();

        availableOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newOrder) -> {
            if (newOrder != null) {
                showOrderDetails(newOrder);
            }
        });

        selectedOrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, newOrder) -> {
            if (newOrder != null) {
                showOrderDetails(newOrder);
            }
        });

        selectedOrdersTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedOrdersTable.getSelectionModel().getSelectedItem() != null) {
                handleUnselectOrder();
            }
        });
    }

    /**
     * Configures the columns and formatting for the available orders table.
     * @author Oğuzhan Aydın
     */
    private void setupAvailableOrdersTable() {
        if (availableOrdersTable == null) return;

        availOrderIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        availTotalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());
        availDateCol.setCellValueFactory(data -> {
            LocalDateTime date = data.getValue().getDeliveryDate();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "Not set");
        });

        availTotalCol.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₺%.2f", item));
            }
        });
    }

    /**
     * Configures the columns for the selected orders table.
     * @author Oğuzhan Aydın
     */
    private void setupSelectedOrdersTable() {
        if (selectedOrdersTable == null) return;

        selOrderIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        selCustomerCol.setCellValueFactory(data -> new SimpleStringProperty("Order #" + data.getValue().getId()));
        selAddressCol.setCellValueFactory(data -> new SimpleStringProperty("See details"));
    }

    /**
     * Configures the columns and rating display for the completed orders table.
     * @author Oğuzhan Aydın
     */
    private void setupCompletedOrdersTable() {
        if (completedOrdersTable == null) return;

        compOrderIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        compDateCol.setCellValueFactory(data -> {
            LocalDateTime date = data.getValue().getDeliveryDate();
            return new SimpleStringProperty(date != null ? date.format(dateFormatter) : "N/A");
        });
        compRatingCol.setCellValueFactory(data -> {
            Integer rating = data.getValue().getCarrierRating();
            double ratingValue = (rating != null && rating >= 1 && rating <= 5) ? rating.doubleValue() : 0.0;
            return new SimpleDoubleProperty(ratingValue).asObject();
        });

        compRatingCol.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0.0) {
                    setText("Not rated");
                } else {
                    setText(item.intValue() + " ⭐");
                }
            }
        });
    }

    /**
     * Fetches available, selected, and completed orders from the service.
     * Populates the tables with the retrieved data.
     * @author Oğuzhan Aydın
     */
    private void loadOrders() {
        if (currentUser == null) return;

        try {
            if (availableOrdersTable != null) {
                ObservableList<Order> available = orderService.getAvailableOrders();
                if (available == null) available = FXCollections.observableArrayList();
                availableOrdersTable.setItems(available);
            }

            if (selectedOrdersTable != null) {
                ObservableList<Order> selected = orderService.getOrdersByCarrierId(currentUser.getId(), "Selected");
                if (selected == null) selected = FXCollections.observableArrayList();
                selectedOrdersTable.setItems(selected);
            }

            if (completedOrdersTable != null) {
                ObservableList<Order> completed = orderService.getOrdersByCarrierId(currentUser.getId(), "Delivered");
                if (completed == null) completed = FXCollections.observableArrayList();
                completedOrdersTable.setItems(completed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (availableOrdersTable != null) availableOrdersTable.setItems(FXCollections.observableArrayList());
            if (selectedOrdersTable != null) selectedOrdersTable.setItems(FXCollections.observableArrayList());
            if (completedOrdersTable != null) completedOrdersTable.setItems(FXCollections.observableArrayList());
        }
    }

    /**
     * Displays detailed information about the selected order in the side panel.
     * @param order The order to display details for.
     * @author Oğuzhan Aydın
     */
    private void showOrderDetails(Order order) {
        if (orderDetailsBox == null || order == null) return;

        orderDetailsBox.getChildren().clear();

        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new javafx.geometry.Insets(15));

        Label titleLabel = new Label("Order Details");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FF4B2B;");

        Label orderIdLabel = new Label("Order ID: " + order.getId());
        Label statusLabel = new Label("Status: " + order.getStatus());
        Label totalLabel = new Label("Total: ₺" + String.format("%.2f", order.getTotal()));
        Label deliveryLabel = new Label("Delivery Date: " +
                (order.getDeliveryDate() != null ? order.getDeliveryDate().format(dateFormatter) : "Not set"));

        Label itemsTitle = new Label("Items:");
        itemsTitle.setStyle("-fx-font-weight: bold; -fx-padding: 10px 0 5px 0;");

        VBox itemsBox = new VBox(5);
        for (var item : order.getItems()) {
            Label itemLabel = new Label(String.format("• %s: %.2f kg @ ₺%.2f (Total: ₺%.2f)",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()));
            itemLabel.setWrapText(true);
            itemsBox.getChildren().add(itemLabel);
        }

        detailsBox.getChildren().addAll(titleLabel, orderIdLabel, statusLabel, totalLabel, deliveryLabel, itemsTitle, itemsBox);
        orderDetailsBox.getChildren().add(detailsBox);
    }

    /**
     * Handles the action of assigning an available order to the current carrier.
     * @author Oğuzhan Aydın
     */
    @FXML
    private void handleSelectOrder() {
        Order selected = availableOrdersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an available order.");
            return;
        }

        if (!"Pending".equals(selected.getStatus())) {
            showAlert(Alert.AlertType.WARNING, "Order Unavailable",
                    "This order has already been selected by another carrier.");
            return;
        }

        try {
            boolean success = orderService.selectOrder(selected.getId(), currentUser.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Order selected successfully.");
                loadOrders();
            } else {
                showAlert(Alert.AlertType.WARNING, "Failed", "Order may have been selected by another carrier.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to select order.");
            e.printStackTrace();
        }
    }

    /**
     * Removes the currently selected order from the carrier's list.
     * Returns the order to the available pool.
     * @author Oğuzhan Aydın
     */
    @FXML
    private void handleUnselectOrder() {
        Order selected = selectedOrdersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to unselect.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Unselect Order");
        confirm.setHeaderText("Unselect Order");
        confirm.setContentText("Are you sure you want to unselect order #" + selected.getId() + "?");
        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/appStyles.css").toExternalForm()
        );

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = orderService.unselectOrder(selected.getId(), currentUser.getId());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Order unselected successfully.");
                        loadOrders();
                        if (orderDetailsBox != null) {
                            orderDetailsBox.getChildren().clear();
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to unselect order.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to unselect order.");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Marks the selected order as delivered and records the delivery time.
     * @author Oğuzhan Aydın
     */
    @FXML
    private void handleCompleteDelivery() {
        Order selected = selectedOrdersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to complete.");
            return;
        }

        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Complete Delivery");
        dialog.setHeaderText("Enter delivery completion date and time");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, LocalDateTime.now().getHour());
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, LocalDateTime.now().getMinute());

        HBox timeBox = new HBox(5);
        timeBox.getChildren().addAll(hourSpinner, new Label(":"), minuteSpinner);

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Delivery Date:"), datePicker,
                new Label("Delivery Time:"), timeBox
        );
        content.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                LocalDate date = datePicker.getValue();
                if (date == null) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Date", "Please select a delivery date.");
                    return null;
                }
                return LocalDateTime.of(date,
                        java.time.LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
            }
            return null;
        });

        dialog.showAndWait().ifPresent(deliveryDateTime -> {
            try {
                boolean success = orderService.completeDelivery(selected.getId(), deliveryDateTime);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Delivery completed successfully.");
                    loadOrders();
                    if (orderDetailsBox != null) {
                        orderDetailsBox.getChildren().clear();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete delivery.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to complete delivery.");
                e.printStackTrace();
            }
        });
    }

    /**
     * Logs out the current user and returns to the login screen.
     * @author Oğuzhan Aydın
     */
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/group16/grocery_app/LoginView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) carrierUsernameLabel.getScene().getWindow();
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

    /**
     * Displays an alert dialog with a specific message.
     * @param type The type of alert (Warning, Information, etc.).
     * @param title The title of the alert window.
     * @param message The content message to display.
     * @author Oğuzhan Aydın
     */
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


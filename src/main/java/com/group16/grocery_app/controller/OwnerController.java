package com.group16.grocery_app.controller;

import com.group16.grocery_app.model.*;
import com.group16.grocery_app.db.service.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import com.group16.grocery_app.model.ConversationUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Controller class for managing the owner's dashboard.
 * Handles product management, carrier management, order overview, coupons, messaging, and reports.
 *
 * @author Yiğit Emre Ünlüçerçi
 */
public class OwnerController {

    private User currentUser;
    private ProductService productService = new ProductService();
    private OrderService orderService = new OrderService();
    private UserService userService = new UserService();
    private MessageService messageService = new MessageService();
    private CouponService couponService = new CouponService();

    private List<ConversationUser> conversationUsers = new java.util.ArrayList<>();

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIdCol;
    @FXML private TableColumn<Product, String> productNameCol;
    @FXML private TableColumn<Product, String> productTypeCol;
    @FXML private TableColumn<Product, Double> productPriceCol;
    @FXML private TableColumn<Product, Double> productStockCol;
    @FXML private TableColumn<Product, Double> productThresholdCol;
    @FXML private Label ownerUsernameLabel;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderIdCol;
    @FXML private TableColumn<Order, String> customerCol;
    @FXML private TableColumn<Order, String> orderDateCol;
    @FXML private TableColumn<Order, String> deliveryDateCol;
    @FXML private TableColumn<Order, String> statusCol;
    @FXML private TableColumn<Order, Double> totalCol;
    @FXML private ComboBox<String> orderFilterCombo;

    @FXML private TableView<User> carriersTable;
    @FXML private TableColumn<User, Integer> carrierIdCol;
    @FXML private TableColumn<User, String> carrierUsernameCol;
    @FXML private TableColumn<User, String> carrierNameCol;
    @FXML private TableColumn<User, Double> carrierRatingCol;

    @FXML private ListView<String> conversationsList;
    @FXML private Label conversationTitleLabel;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private VBox messagesBox;
    @FXML private MFXTextField messageInput;

    @FXML private TableView<Coupon> couponsTable;
    @FXML private TableColumn<Coupon, String> couponCodeCol;
    @FXML private TableColumn<Coupon, Double> couponDiscountCol;
    @FXML private TableColumn<Coupon, String> couponExpiryCol;
    @FXML private TableColumn<Coupon, Boolean> couponActiveCol;
    @FXML private MFXTextField loyaltyThresholdField;

    @FXML private Label totalRevenueLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label activeCarriersLabel;
    @FXML private PieChart ordersByStatusChart;
    @FXML private BarChart<String, Number> monthlyRevenueChart;

    /**
     * Sets the current user for the owner session and initializes the dashboard.
     * Updates the username label and loads initial data.
     *
     * @param user the logged-in owner user
     * @author Yiğit Emre Ünlüçerçi
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (ownerUsernameLabel != null) {
            ownerUsernameLabel.setText(user.getUsername());
        }
        initialize();
    }

    /**
     * Initializes the owner dashboard when the view is loaded.
     * Sets up tables, messaging, and loads all required data.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    public void initialize() {
        if (currentUser == null) return;

        setupProductsTable();
        setupOrdersTable();
        setupCarriersTable();
        setupCouponsTable();
        setupMessages();
        loadData();
    }

    /**
     * Configures the products table columns and formatting.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void setupProductsTable() {
        if (productsTable == null) return;

        productIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        productNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        productTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType().toString()));
        productPriceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        productStockCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getStock()).asObject());
        productThresholdCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getThreshold()).asObject());

        productPriceCol.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₺%.2f", item));
            }
        });
    }

    /**
     * Configures the orders table columns and formatting.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void setupOrdersTable() {
        if (ordersTable == null) return;

        orderIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        customerCol.setCellValueFactory(data -> new SimpleStringProperty("Customer #" + data.getValue().getId()));
        orderDateCol.setCellValueFactory(data -> {
            LocalDate date = data.getValue().getCreatedAt() != null ?
                    data.getValue().getCreatedAt().toLocalDate() : null;
            return new SimpleStringProperty(date != null ? date.toString() : "N/A");
        });
        deliveryDateCol.setCellValueFactory(data -> {
            LocalDate date = data.getValue().getDeliveryDate() != null ?
                    data.getValue().getDeliveryDate().toLocalDate() : null;
            return new SimpleStringProperty(date != null ? date.toString() : "Not set");
        });
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        totalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotal()).asObject());

        totalCol.setCellFactory(column -> new TableCell<Order, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₺%.2f", item));
            }
        });
    }

    /**
     * Configures the carriers table columns and rating display.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void setupCarriersTable() {
        if (carriersTable == null) return;

        carrierIdCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        carrierUsernameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        carrierNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFirstName() + " " + data.getValue().getLastName()));

        carrierRatingCol.setCellValueFactory(data -> {
            int carrierId = data.getValue().getId();
            double avgRating = orderService.getAverageCarrierRating(carrierId);
            return new SimpleDoubleProperty(avgRating).asObject();
        });

        carrierRatingCol.setCellFactory(column -> new TableCell<User, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item < 0) {
                    setText("No ratings");
                } else {
                    setText(String.format("%.1f ⭐", item));
                }
            }
        });
    }

    /**
     * Configures the coupons table columns and formatting.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void setupCouponsTable() {
        if (couponsTable == null) return;

        couponCodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCode()));
        couponDiscountCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getDiscountAmount()).asObject());
        couponExpiryCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getExpiryDate() != null ? data.getValue().getExpiryDate().toString() : "N/A"));
        couponActiveCol.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().isActive()));

        couponDiscountCol.setCellFactory(column -> new TableCell<Coupon, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₺%.2f", item));
            }
        });
    }

    /**
     * Sets up the messaging UI event listeners and loads conversation list.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void setupMessages() {
        if (conversationsList == null) return;
        conversationsList.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                loadConversation(newVal);
            }
        });
        loadConversations();
    }

    /**
     * Loads available conversations for the current owner.
     * Populates the conversation list and caches conversation user IDs.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadConversations() {
        if (conversationsList != null && currentUser != null) {
            ObservableList<String> conversations = messageService.getConversations(currentUser.getId());
            conversationsList.setItems(conversations);
            conversationUsers = messageService.getConversationUserIds(currentUser.getId());
        }
    }

    /**
     * Loads all owner dashboard data.
     * Calls individual loaders for products, orders, carriers, coupons, and reports.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadData() {
        loadProducts();
        loadOrders();
        loadCarriers();
        loadCoupons();
        loadReports();
    }

    /**
     * Loads all coupons and populates the coupons table.
     * Sets an empty list if an error occurs.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadCoupons() {
        if (couponsTable != null) {
            try {
                ObservableList<Coupon> coupons = couponService.getAllCoupons();
                if (coupons == null) coupons = FXCollections.observableArrayList();
                couponsTable.setItems(coupons);
            } catch (Exception e) {
                e.printStackTrace();
                couponsTable.setItems(FXCollections.observableArrayList());
            }
        }
    }

    /**
     * Loads all products and populates the products table.
     * Sets an empty list if an error occurs.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadProducts() {
        if (productsTable != null) {
            try {
                ObservableList<Product> products = productService.getAllProducts();
                if (products == null) products = FXCollections.observableArrayList();
                productsTable.setItems(products);
            } catch (Exception e) {
                e.printStackTrace();
                productsTable.setItems(FXCollections.observableArrayList());
            }
        }
    }

    /**
     * Loads all orders and populates the orders table.
     * Sets an empty list if an error occurs.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadOrders() {
        if (ordersTable != null) {
            try {
                ObservableList<Order> orders = orderService.getAllOrders();
                if (orders == null) orders = FXCollections.observableArrayList();
                ordersTable.setItems(orders);
            } catch (Exception e) {
                e.printStackTrace();
                ordersTable.setItems(FXCollections.observableArrayList());
            }
        }
    }

    /**
     * Loads all carriers and populates the carriers table.
     * Sets an empty list if an error occurs.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadCarriers() {
        if (carriersTable != null) {
            try {
                ObservableList<User> carriers = userService.getCarriers();
                if (carriers == null) carriers = FXCollections.observableArrayList();
                carriersTable.setItems(carriers);
            } catch (Exception e) {
                e.printStackTrace();
                carriersTable.setItems(FXCollections.observableArrayList());
            }
        }
    }

    /**
     * Loads report summary metrics and updates charts.
     * Calculates total revenue, order counts, active carriers, and chart data.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadReports() {
        try {
            ObservableList<Order> allOrders = orderService.getAllOrders();
            ObservableList<User> carriers = userService.getCarriers();

            if (allOrders == null) allOrders = FXCollections.observableArrayList();
            if (carriers == null) carriers = FXCollections.observableArrayList();

            double totalRevenue = allOrders.stream()
                    .mapToDouble(Order::getTotal)
                    .sum();

            int totalOrders = allOrders.size();
            int activeCarriers = carriers.size();

            if (totalRevenueLabel != null) {
                totalRevenueLabel.setText(String.format("₺%.2f", totalRevenue));
            }
            if (totalOrdersLabel != null) {
                totalOrdersLabel.setText(String.valueOf(totalOrders));
            }
            if (activeCarriersLabel != null) {
                activeCarriersLabel.setText(String.valueOf(activeCarriers));
            }

            updateOrdersByStatusChart(allOrders);
            updateMonthlyRevenueChart(allOrders);
        } catch (Exception e) {
            e.printStackTrace();
            if (totalRevenueLabel != null) totalRevenueLabel.setText("₺0.00");
            if (totalOrdersLabel != null) totalOrdersLabel.setText("0");
            if (activeCarriersLabel != null) activeCarriersLabel.setText("0");
        }
    }

    /**
     * Updates the "Orders by Status" pie chart using the given orders.
     *
     * @param orders the list of orders to analyze
     * @author Yiğit Emre Ünlüçerçi
     */
    private void updateOrdersByStatusChart(ObservableList<Order> orders) {
        if (ordersByStatusChart == null) return;

        int pendingCount = 0;
        int selectedCount = 0;
        int deliveredCount = 0;
        int cancelledCount = 0;

        for (Order order : orders) {
            String status = order.getStatus();
            if (status != null) {
                if ("Pending".equals(status)) {
                    pendingCount++;
                } else if ("Selected".equals(status)) {
                    selectedCount++;
                } else if ("Delivered".equals(status)) {
                    deliveredCount++;
                } else if ("Cancelled".equals(status)) {
                    cancelledCount++;
                }
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        final PieChart.Data[] pendingData = {null};
        final PieChart.Data[] selectedData = {null};
        final PieChart.Data[] deliveredData = {null};
        final PieChart.Data[] cancelledData = {null};

        if (pendingCount > 0) {
            pendingData[0] = new PieChart.Data("Pending (" + pendingCount + ")", pendingCount);
            pieChartData.add(pendingData[0]);
        }
        if (selectedCount > 0) {
            selectedData[0] = new PieChart.Data("Selected (" + selectedCount + ")", selectedCount);
            pieChartData.add(selectedData[0]);
        }
        if (deliveredCount > 0) {
            deliveredData[0] = new PieChart.Data("Delivered (" + deliveredCount + ")", deliveredCount);
            pieChartData.add(deliveredData[0]);
        }
        if (cancelledCount > 0) {
            cancelledData[0] = new PieChart.Data("Cancelled (" + cancelledCount + ")", cancelledCount);
            pieChartData.add(cancelledData[0]);
        }

        ordersByStatusChart.setData(pieChartData);
        ordersByStatusChart.setTitle("Orders by Status");
        ordersByStatusChart.setLegendVisible(true);
        ordersByStatusChart.setLabelsVisible(true);

        javafx.application.Platform.runLater(() -> {
            if (pendingData[0] != null && pendingData[0].getNode() != null) {
                pendingData[0].getNode().setStyle("-fx-pie-color: #FFA500;");
            }
            if (selectedData[0] != null && selectedData[0].getNode() != null) {
                selectedData[0].getNode().setStyle("-fx-pie-color: #4169E1;");
            }
            if (deliveredData[0] != null && deliveredData[0].getNode() != null) {
                deliveredData[0].getNode().setStyle("-fx-pie-color: #32CD32;");
            }
            if (cancelledData[0] != null && cancelledData[0].getNode() != null) {
                cancelledData[0].getNode().setStyle("-fx-pie-color: #DC143C;");
            }
        });
    }

    /**
     * Updates the monthly revenue bar chart using the given orders.
     * Groups order totals by year-month and displays them in ascending time order.
     *
     * @param orders the list of orders to analyze
     * @author Yiğit Emre Ünlüçerçi
     */
    private void updateMonthlyRevenueChart(ObservableList<Order> orders) {
        if (monthlyRevenueChart == null) return;

        List<String> months = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        for (Order order : orders) {
            if (order.getCreatedAt() != null) {
                String month = order.getCreatedAt().format(monthFormatter);
                boolean found = false;
                for (int i = 0; i < months.size(); i++) {
                    if (months.get(i).equals(month)) {
                        revenues.set(i, revenues.get(i) + order.getTotal());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    months.add(month);
                    revenues.add(order.getTotal());
                }
            }
        }

        monthlyRevenueChart.setTitle("Monthly Revenue");
        monthlyRevenueChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        for (int i = 0; i < months.size() - 1; i++) {
            for (int j = 0; j < months.size() - i - 1; j++) {
                if (months.get(j).compareTo(months.get(j + 1)) > 0) {
                    String tempMonth = months.get(j);
                    months.set(j, months.get(j + 1));
                    months.set(j + 1, tempMonth);
                    Double tempRev = revenues.get(j);
                    revenues.set(j, revenues.get(j + 1));
                    revenues.set(j + 1, tempRev);
                }
            }
        }

        for (int i = 0; i < months.size(); i++) {
            try {
                YearMonth yearMonth = YearMonth.parse(months.get(i), monthFormatter);
                String displayMonth = yearMonth.format(displayFormatter);
                XYChart.Data<String, Number> data = new XYChart.Data<>(displayMonth, revenues.get(i));
                series.getData().add(data);
            } catch (Exception e) {
                series.getData().add(new XYChart.Data<>(months.get(i), revenues.get(i)));
            }
        }

        monthlyRevenueChart.getData().clear();
        monthlyRevenueChart.getData().add(series);

        monthlyRevenueChart.setCategoryGap(20);
        monthlyRevenueChart.setBarGap(5);

        javafx.application.Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #FF4B2B;");
                }
            }
        });
    }

    /**
     * Loads and displays messages for the selected conversation.
     * Updates the conversation title and renders messages in the message box.
     *
     * @param customerName the username shown in the conversation list
     * @author Yiğit Emre Ünlüçerçi
     */
    private void loadConversation(String customerName) {
        if (conversationTitleLabel != null) {
            conversationTitleLabel.setText("Conversation with " + customerName);
        }
        if (messagesBox != null && currentUser != null) {
            messagesBox.getChildren().clear();

            Integer customerId = null;
            for (ConversationUser user : conversationUsers) {
                if (user.getUsername().equals(customerName)) {
                    customerId = user.getUserId();
                    break;
                }
            }
            if (customerId != null) {
                ObservableList<Message> messages = messageService.getMessagesBetween(currentUser.getId(), customerId);
                for (Message msg : messages) {
                    Label msgLabel = new Label(msg.getContent());
                    msgLabel.setWrapText(true);
                    msgLabel.setMaxWidth(500);
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
            }
        }
    }

    /**
     * Opens the dialog to add a new product.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleAddProduct() {
        showProductDialog(null);
    }

    /**
     * Opens the dialog to update the selected product.
     * Shows a warning if no product is selected.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleUpdateProduct() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to update.");
            return;
        }
        showProductDialog(selected);
    }

    /**
     * Removes the selected product after confirmation.
     * Reloads product data on success.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleRemoveProduct() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a product to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove Product");
        confirm.setContentText("Are you sure you want to remove " + selected.getName() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = productService.removeProduct(selected.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product removed successfully.");
                loadProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove product.");
            }
        }
    }

    /**
     * Displays a dialog for adding or updating a product.
     * Validates input fields and optionally allows selecting an image file.
     *
     * @param product the product to update, or null to add a new product
     * @author Yiğit Emre Ünlüçerçi
     */
    private void showProductDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Add Product" : "Update Product");

        TextField nameField = new TextField(product != null ? product.getName() : "");
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("FRUIT", "VEGETABLE"));
        if (product != null) typeCombo.setValue(product.getType().toString());
        TextField priceField = new TextField(product != null ? String.valueOf(product.getPrice()) : "");
        TextField stockField = new TextField(product != null ? String.valueOf(product.getStock()) : "");
        TextField thresholdField = new TextField(product != null ? String.valueOf(product.getThreshold()) : "");

        javafx.scene.control.Button selectImageButton = new javafx.scene.control.Button("Select Image");
        Label imagePathLabel = new Label("No image selected");
        imagePathLabel.setWrapText(true);
        imagePathLabel.setMaxWidth(300);

        ImageView imagePreview = new ImageView();
        imagePreview.setFitWidth(150);
        imagePreview.setFitHeight(150);
        imagePreview.setPreserveRatio(true);
        imagePreview.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1px;");

        if (product != null && product.getImage() != null) {
            imagePreview.setImage(product.getImage());
            imagePathLabel.setText("Current product image");
        }

        final File[] selectedImageFile = {null};

        selectImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Product Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                selectedImageFile[0] = file;
                imagePathLabel.setText("Selected: " + file.getName());

                try {
                    Image previewImage = new Image(file.toURI().toString());
                    imagePreview.setImage(previewImage);
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.WARNING, "Image Error", "Could not load image preview: " + ex.getMessage());
                }
            }
        });

        VBox imageBox = new VBox(5);
        imageBox.getChildren().addAll(
            new Label("Product Image:"),
            selectImageButton,
            imagePathLabel,
            imagePreview
        );

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Name:"), nameField,
                new Label("Type:"), typeCombo,
                new Label("Price:"), priceField,
                new Label("Stock:"), stockField,
                new Label("Threshold:"), thresholdField,
                new javafx.scene.control.Separator(),
                imageBox
        );
        content.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                double price = Double.parseDouble(priceField.getText());
                double stock = Double.parseDouble(stockField.getText());
                double threshold = Double.parseDouble(thresholdField.getText());

                if (price <= 0 || stock < 0 || threshold <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "All numeric values must be positive.");
                    event.consume();
                    return;
                }

                ProductType type = ProductType.valueOf(typeCombo.getValue());
                boolean success;

                File imageFile = selectedImageFile[0];

                if (product == null) {
                    success = productService.addProduct(nameField.getText(), type, price, stock, threshold, imageFile);
                } else {

                    success = productService.updateProduct(product.getId(), nameField.getText(), type, price, stock, threshold, imageFile);
                }

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            (product == null ? "Product added" : "Product updated") + " successfully.");
                    loadProducts();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save product.");
                    event.consume();
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers.");
                event.consume();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(button -> button == ButtonType.OK ? product : null);

        dialog.showAndWait();
    }

    /**
     * Opens the dialog to add a new carrier.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleAddCarrier() {
        showCarrierDialog(null);
    }

    /**
     * Removes the selected carrier after confirmation.
     * Reloads carrier data on success.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleRemoveCarrier() {
        User selected = carriersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a carrier to remove.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove Carrier");
        confirm.setContentText("Are you sure you want to remove carrier " + selected.getUsername() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = userService.removeCarrier(selected.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Carrier removed successfully.");
                loadCarriers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove carrier.");
            }
        }
    }

    /**
     * Displays a dialog for adding or updating a carrier.
     * Validates carrier input and creates a new carrier when carrier is null.
     *
     * @param carrier the carrier to update, or null to add a new carrier
     * @author Yiğit Emre Ünlüçerçi
     */
    private void showCarrierDialog(User carrier) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(carrier == null ? "Add Carrier" : "Update Carrier");

        TextField usernameField = new TextField(carrier != null ? carrier.getUsername() : "");
        TextField firstNameField = new TextField(carrier != null ? carrier.getFirstName() : "");
        TextField lastNameField = new TextField(carrier != null ? carrier.getLastName() : "");
        PasswordField passwordField = new PasswordField();

        if (carrier != null) {
            usernameField.setDisable(true);
            passwordField.setPromptText("Leave blank to keep current");
        }

        VBox content = new VBox(10);
        Label passwordHint = new Label("Password must be at least 8 characters long");
        passwordHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");
        content.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("First Name:"), firstNameField,
                new Label("Last Name:"), lastNameField,
                new Label("Password:"), passwordField,
                passwordHint
        );
        content.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (carrier == null) {
                String username = usernameField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String password = passwordField.getText();

                if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all fields.");
                    event.consume();
                    return;
                }

                if (!username.matches("^[a-zA-Z0-9_]+$")) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Username",
                            "Username must contain only letters, numbers, and underscores.");
                    event.consume();
                    return;
                }

                if (!firstName.matches("^[a-zA-Z]+$") || !lastName.matches("^[a-zA-Z]+$")) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Name",
                            "First name and last name must contain only letters.");
                    event.consume();
                    return;
                }

                if (password.length() < 8) {
                    showAlert(Alert.AlertType.WARNING, "Invalid Password",
                            "Password must be at least 8 characters long.");
                    event.consume();
                    return;
                }

                try {
                    boolean success = userService.createCarrier(username, password, firstName, lastName);
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Carrier added successfully.");
                        loadCarriers();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Failed to add carrier. The username may already exist. Please choose a different username and try again.");
                        event.consume();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "An error occurred while adding carrier: " + e.getMessage());
                    event.consume();
                }
            }
        });

        dialog.setResultConverter(button -> button == ButtonType.OK ? carrier : null);

        dialog.showAndWait();
    }

    /**
     * Sends a message to the selected customer in the conversations list.
     * Refreshes the conversation view on success.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText();
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        String selectedCustomer = conversationsList.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a conversation.");
            return;
        }

        Integer customerId = null;
        for (ConversationUser user : conversationUsers) {
            if (user.getUsername().equals(selectedCustomer)) {
                customerId = user.getUserId();
                break;
            }
        }
        if (customerId == null || currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to send message.");
            return;
        }

        boolean success = messageService.sendMessage(currentUser.getId(), customerId, message.trim());
        if (success) {
            messageInput.clear();
            loadConversation(selectedCustomer);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send message.");
        }
    }

    /**
     * Opens the dialog to add a new coupon.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleAddCoupon() {
        showCouponDialog(null);
    }

    /**
     * Opens the dialog to update the selected coupon.
     * Shows a warning if no coupon is selected.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleUpdateCoupon() {
        Coupon selected = couponsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a coupon to update.");
            return;
        }
        showCouponDialog(selected);
    }

    /**
     * Deletes the selected coupon after confirmation.
     * Reloads coupon data on success.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleDeleteCoupon() {
        Coupon selected = couponsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a coupon to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Coupon");
        confirm.setContentText("Are you sure you want to delete coupon " + selected.getCode() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = couponService.deleteCoupon(selected.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Coupon deleted successfully.");
                loadCoupons();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete coupon.");
            }
        }
    }

    /**
     * Displays a dialog for adding or updating a coupon.
     * Validates input fields and saves the coupon via the coupon service.
     *
     * @param coupon the coupon to update, or null to add a new coupon
     * @author Yiğit Emre Ünlüçerçi
     */
    private void showCouponDialog(Coupon coupon) {
        Dialog<Coupon> dialog = new Dialog<>();
        dialog.setTitle(coupon == null ? "Add Coupon" : "Update Coupon");

        TextField codeField = new TextField(coupon != null ? coupon.getCode() : "");
        TextField discountField = new TextField(coupon != null ? String.valueOf(coupon.getDiscountAmount()) : "");
        DatePicker expiryPicker = new DatePicker();
        if (coupon != null && coupon.getExpiryDate() != null) {
            expiryPicker.setValue(coupon.getExpiryDate());
        } else {
            expiryPicker.setValue(java.time.LocalDate.now().plusMonths(1));
        }

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Code:"), codeField,
                new Label("Discount Amount:"), discountField,
                new Label("Expiry Date:"), expiryPicker
        );
        content.setPadding(new javafx.geometry.Insets(20));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        javafx.scene.control.Button okButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                double discount = Double.parseDouble(discountField.getText());
                if (discount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Discount must be positive.");
                    event.consume();
                    return;
                }
                if (codeField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a coupon code.");
                    event.consume();
                    return;
                }
                if (expiryPicker.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select an expiry date.");
                    event.consume();
                    return;
                }

                boolean success;
                if (coupon == null) {
                    success = couponService.addCoupon(codeField.getText().trim(), discount, expiryPicker.getValue());
                } else {
                    success = couponService.updateCoupon(coupon.getId(), codeField.getText().trim(),
                            discount, expiryPicker.getValue(), coupon.isActive());
                }

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Coupon saved successfully.");
                    loadCoupons();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save coupon.");
                    event.consume();
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers.");
                event.consume();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
                event.consume();
            }
        });

        dialog.setResultConverter(button -> button == ButtonType.OK ? coupon : null);

        dialog.showAndWait();
    }

    /**
     * Handles loyalty threshold update request from the owner.
     * Currently shows an informational message because the update is not implemented.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleUpdateLoyalty() {
        String threshold = loyaltyThresholdField.getText();
        if (threshold == null || threshold.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Input", "Please enter a loyalty threshold.");
            return;
        }

        try {
            int thresholdValue = Integer.parseInt(threshold);
            if (thresholdValue < 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Threshold must be non-negative.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Note",
                    "Loyalty threshold update not yet implemented.\n" +
                            "Currently, loyalty discount (5%) is applied when customers have 5+ completed orders.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
        }
    }

    /**
     * Refreshes the reports section by reloading report data and charts.
     *
     * @author Yiğit Emre Ünlüçerçi
     */
    @FXML
    private void handleRefreshReports() {
        loadReports();
    }

    /**
     * Logs out the current user and navigates back to the login view.
     * Shows a confirmation dialog before performing logout.
     *
     * @author Yiğit Emre Ünlüçerçi
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) (ownerUsernameLabel != null ? ownerUsernameLabel.getScene().getWindow() :
                            productsTable.getScene().getWindow());
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
     * Displays an alert dialog with the given type, title, and message.
     *
     * @param type the type of the alert (e.g., Error, Information)
     * @param title the title of the alert window
     * @param message the content message
     * @author Yiğit Emre Ünlüçerçi
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

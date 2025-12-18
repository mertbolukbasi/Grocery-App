package com.group16.grocery_app.controller;

import com.group16.grocery_app.db.service.OrderService;
import com.group16.grocery_app.model.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class CarrierController {

    // --- SERVICE ---
    private OrderService orderService;

    private int currentCarrierId;

    // --- FXML BİLEŞENLERİ ---
    @FXML private VBox pageAvailable;
    @FXML private VBox pageCurrent;
    @FXML private VBox pageHistory;

    @FXML private Button tabBtnAvailable;
    @FXML private Button tabBtnCurrent;
    @FXML private Button tabBtnHistory;

    @FXML private Button btnLogout;
    @FXML private Button btnTakeOrder;
    @FXML private Button btnComplete;
    @FXML private DatePicker datePickerDelivery;

    // --- TABLOLAR ---
    @FXML private TableView<Order> tableAvailable;
    @FXML private TableColumn<Order, Integer> colAvId;
    @FXML private TableColumn<Order, String> colAvCust;
    @FXML private TableColumn<Order, String> colAvAddr;
    @FXML private TableColumn<Order, Double> colAvTotal;

    @FXML private TableView<Order> tableCurrent;
    @FXML private TableColumn<Order, Integer> colCurId;
    @FXML private TableColumn<Order, String> colCurAddr;
    @FXML private TableColumn<Order, Double> colCurTotal;

    @FXML private TableView<Order> tableCompleted;
    @FXML private TableColumn<Order, Integer> colCompId;
    @FXML private TableColumn<Order, String> colCompCust;
    @FXML private TableColumn<Order, Double> colCompTotal;

    // --- BAŞLANGIÇ (INITIALIZE) ---
    @FXML
    public void initialize() {
        orderService = new OrderService();

        showPage(pageAvailable);
        updateButtonStyles(tabBtnAvailable);
        setupTableColumns();

        refreshTables();

        btnTakeOrder.setOnAction(e -> handleTakeOrder());
        btnComplete.setOnAction(e -> handleCompleteOrder());

        btnLogout.setOnAction(e -> System.out.println("Logout")); //Logout class'ı yazılınca buraya eklenecek.
    }

    public void setCarrierId(int id) {
        this.currentCarrierId = id;
        refreshTables();
    }

    private void setupTableColumns() {
        colAvId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colAvCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colAvAddr.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        colAvTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        colCurId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCurAddr.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        colCurTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        colCompId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCompCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colCompTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
    }

    private void refreshTables() {
        // ID henüz atanmamışsa (0 ise) veritabanına boşuna gitme
        if (currentCarrierId == 0) {
            // Sadece müsait siparişleri çekebiliriz (ID gerektirmez)
            tableAvailable.getItems().setAll(orderService.getAvailableOrders());
            return;
        }

        tableAvailable.getItems().setAll(orderService.getAvailableOrders());
        tableCurrent.getItems().setAll(orderService.getCurrentOrders(currentCarrierId));
        tableCompleted.getItems().setAll(orderService.getOrderHistory(currentCarrierId));
    }

    // --- BUTON İŞLEVLERİ ---

    private void handleTakeOrder() {
        Order selectedOrder = tableAvailable.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an order to take.");
            return;
        }

        if (currentCarrierId == 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "No carrier logged in!");
            return;
        }

        String result = orderService.takeOrder(selectedOrder.getOrderId(), currentCarrierId);

        if ("SUCCESS".equals(result)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "You have taken the order!");
            refreshTables();
            tabBtnCurrent.fire();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", result);
            refreshTables();
        }
    }

    private void handleCompleteOrder() {
        Order selectedOrder = tableCurrent.getSelectionModel().getSelectedItem();

        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an order to complete.");
            return;
        }

        boolean success = orderService.completeDelivery(selectedOrder.getOrderId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order delivered successfully!");
            refreshTables();
            tabBtnHistory.fire();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not complete the order.");
        }
    }

    // --- SAYFA GEÇİŞLERİ ---
    @FXML
    private void handleTabSwitch(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton.getUserData() == null) return;

        String targetPageName = (String) clickedButton.getUserData();
        updateButtonStyles(clickedButton);

        if ("pageAvailable".equals(targetPageName)) showPage(pageAvailable);
        else if ("pageCurrent".equals(targetPageName)) showPage(pageCurrent);
        else if ("pageHistory".equals(targetPageName)) showPage(pageHistory);
    }

    private void showPage(VBox targetPage) {
        pageAvailable.setVisible(false);
        pageCurrent.setVisible(false);
        pageHistory.setVisible(false);
        targetPage.setVisible(true);
        targetPage.toFront();
    }

    private void updateButtonStyles(Button activeButton) {
        resetButtonStyle(tabBtnAvailable);
        resetButtonStyle(tabBtnCurrent);
        resetButtonStyle(tabBtnHistory);
        activeButton.getStyleClass().removeAll("tab-button");
        activeButton.getStyleClass().add("tab-button-active");
    }

    private void resetButtonStyle(Button btn) {
        btn.getStyleClass().removeAll("tab-button-active");
        btn.getStyleClass().add("tab-button");
    }

    // --- STANDART ALERT METODU ---
    // Bu metod sayesinde tek satırda uyarı verebiliyoruz. Bu kısmı Mert'e sor duruma göre kaldır ve geri kalan kısmı güncelle.
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait(); // Pencereyi göster ve kullanıcı kapatana kadar bekle
    }
}
package com.group16.grocery_app.controller;

import com.group16.grocery_app.model.Cart;
import com.group16.grocery_app.model.CartItem;
import com.group16.grocery_app.model.Order;
import com.group16.grocery_app.model.User;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import com.group16.grocery_app.db.service.OrderService;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import javafx.scene.control.TextArea;

public class CartController {

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> nameCol;

    @FXML
    private TableColumn<CartItem, Double> quantityCol;

    @FXML
    private TableColumn<CartItem, Double> priceCol;

    @FXML
    private TableColumn<CartItem, Double> totalCol;

    @FXML
    private Label totalLabel;

    @FXML
    private Label vatLabel;

    @FXML
    private Label grandTotalLabel;

    @FXML
    private MFXTextField couponCodeField;

    @FXML
    private Label appliedCouponLabel;

    @FXML
    private Label couponDiscountLabel;

    @FXML
    private Label loyaltyDiscountLabel;

    @FXML
    private ListView<String> availableCouponsList;

    private Cart cart;
    private User currentUser;

    private final OrderService orderService = new OrderService();
    private final com.group16.grocery_app.db.service.UserCouponService userCouponService =
            new com.group16.grocery_app.db.service.UserCouponService();


    private static final double VAT_RATE = 0.18;
    private static final double MIN_CART_VALUE = 200.0;

    public void setCart(Cart cart) {
        this.cart = cart;
        cart.refreshEffectivePrices();
        cartTable.setItems(cart.getItems());

        updateTotals();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        calculateLoyaltyDiscount();
        loadAvailableCoupons();
        updateTotals();
    }

    @FXML
    public void initialize() {

        cartTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cartTable.setEditable(true);
        quantityCol.setEditable(true);

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        quantityCol.setOnEditCommit(event -> {

            CartItem item = event.getRowValue();
            double newValue = event.getNewValue();

            if (newValue <= 0) {
                cartTable.refresh();
                return;
            }

            item.setQuantity(newValue);
            cart.refreshEffectivePrices();
            calculateLoyaltyDiscount();
            cartTable.refresh();
            updateTotals();
        });

        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getName()));

        quantityCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getQuantity()).asObject());

        priceCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getEffectivePrice()).asObject());

        totalCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTotalPrice()).asObject());
    }

    private void updateTotals() {
        double subtotal = cart.getSubtotal();
        double couponDiscount = cart.getCouponDiscount();
        double loyaltyDiscount = cart.getLoyaltyDiscount();
        double afterDiscounts = cart.getTotalAfterDiscounts();
        double vat = afterDiscounts * VAT_RATE;
        double grandTotal = afterDiscounts + vat;

        totalLabel.setText(String.format("Subtotal: ₺%.2f", subtotal));

        if (couponDiscount > 0) {
            couponDiscountLabel.setText(String.format("Coupon Discount (-₺%.2f): -₺%.2f",
                    couponDiscount, couponDiscount));
            couponDiscountLabel.setVisible(true);
        } else {
            couponDiscountLabel.setVisible(false);
        }

        if (loyaltyDiscount > 0) {
            loyaltyDiscountLabel.setText(String.format("Loyalty Discount (-₺%.2f): -₺%.2f",
                    loyaltyDiscount, loyaltyDiscount));
            loyaltyDiscountLabel.setVisible(true);
        } else {
            loyaltyDiscountLabel.setVisible(false);
        }

        vatLabel.setText(String.format("VAT (18%%): ₺%.2f", vat));
        grandTotalLabel.setText(String.format("Total: ₺%.2f", grandTotal));
    }

    private void calculateLoyaltyDiscount() {
        if (currentUser == null) return;

        int completedOrders = orderService.getCompletedOrdersCount(currentUser.getId());
        double subtotal = cart.getSubtotal();

        if (completedOrders >= 5) {
            double discount = subtotal * 0.05;
            cart.setLoyaltyDiscount(discount);
        } else {
            cart.setLoyaltyDiscount(0.0);
        }
    }

    private void loadAvailableCoupons() {
        if (currentUser == null || availableCouponsList == null) {
            return;
        }

        try {
            ObservableList<com.group16.grocery_app.model.Coupon> coupons = userCouponService.getUserCoupons(currentUser.getId());
            ObservableList<String> couponStrings = FXCollections.observableArrayList();

            for (com.group16.grocery_app.model.Coupon coupon : coupons) {
                String couponText = String.format("%s - ₺%.2f off", coupon.getCode(), coupon.getDiscountAmount());
                couponStrings.add(couponText);
            }

            availableCouponsList.setItems(couponStrings);

            availableCouponsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = availableCouponsList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        String code = selected.split(" - ")[0];
                        couponCodeField.setText(code);
                        handleApplyCoupon();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplyCoupon() {
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please log in to use coupons.");
            return;
        }

        String couponCode = couponCodeField.getText();
        if (couponCode == null || couponCode.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a coupon code.");
            return;
        }

        com.group16.grocery_app.model.Coupon coupon = userCouponService.getCouponByCode(couponCode.trim());

        if (coupon == null) {
            showAlert(Alert.AlertType.WARNING, "Invalid Coupon", "Coupon code not found or expired.");
            return;
        }

        boolean hasCoupon = userCouponService.hasUnusedCoupon(currentUser.getId(), coupon.getId());
        if (!hasCoupon) {
            showAlert(Alert.AlertType.WARNING, "Invalid Coupon", "You don't have this coupon or it has been used.");
            return;
        }

        cart.setCouponDiscount(coupon.getDiscountAmount(), coupon.getCode());
        appliedCouponLabel.setText("Applied: " + coupon.getCode() + " (-₺" +
                String.format("%.2f", coupon.getDiscountAmount()) + ")");
        updateTotals();
        couponCodeField.clear();

        showAlert(Alert.AlertType.INFORMATION, "Coupon Applied",
                "Coupon " + coupon.getCode() + " applied successfully!");

        loadAvailableCoupons();
    }

    @FXML
    private void handleRemove() {

        ObservableList<CartItem> selectedItems = FXCollections.observableArrayList(cartTable.getSelectionModel().getSelectedItems());

        if (selectedItems.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "No Selection",
                    "Please select item(s) to remove."
            );
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText("Remove product(s)");
        confirm.setContentText(
                "Are you sure you want to remove " +
                        selectedItems.size() + " item(s) from cart?"
        );

        confirm.getDialogPane().getStylesheets().add(
                getClass().getResource("/app.css").toExternalForm()
        );

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            for (CartItem item : selectedItems) {
                cart.removeProduct(item.getProduct());
            }

            cartTable.getSelectionModel().clearSelection();

            cartTable.refresh();

            updateTotals();
        }
    }
    @FXML
    private void handleCheckout() {

        if (cart.getItems().isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Empty Cart",
                    "Your cart is empty."
            );
            return;
        }

        double subtotal = cart.getTotal();
        if (subtotal < MIN_CART_VALUE) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Minimum Cart Value",
                    "Minimum order amount (before VAT) is ₺" + MIN_CART_VALUE
            );
            return;
        }

        LocalDateTime deliveryDateTime = showDeliveryDateTimeDialog();
        if (deliveryDateTime == null) {
            return;
        }

        try {
            Order order = cart.checkout(VAT_RATE, deliveryDateTime);

            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "User information is missing. Please log in again.");
                return;
            }
            String customerName = currentUser.getFirstName() + " " + currentUser.getLastName();
            orderService.placeOrderWithInvoice(order, currentUser.getId(), deliveryDateTime,
                    customerName, currentUser.getAddress());

            String appliedCouponCode = cart.getAppliedCouponCode();
            if (appliedCouponCode != null && !appliedCouponCode.trim().isEmpty()) {
                com.group16.grocery_app.model.Coupon coupon = userCouponService.getCouponByCode(appliedCouponCode);
                if (coupon != null) {
                    userCouponService.useCoupon(currentUser.getId(), coupon.getId());
                }
            }

            com.group16.grocery_app.db.service.UserService userService =
                    new com.group16.grocery_app.db.service.UserService();
            userService.incrementLoyaltyPoints(currentUser.getId(), 1);

            cart.clear();

            loadAvailableCoupons();

            showAlert(Alert.AlertType.INFORMATION, "Order Successful", "Your order has been placed!\nTotal (incl. VAT): ₺" + String.format("%.2f", order.getTotal()));

            cartTable.refresh();
            totalLabel.setText("Subtotal: ₺0.00");
            couponDiscountLabel.setVisible(false);
            loyaltyDiscountLabel.setVisible(false);
            appliedCouponLabel.setText("");
            vatLabel.setText("VAT (18%): ₺0.00");
            grandTotalLabel.setText("Total: ₺0.00");
            couponCodeField.clear();

        } catch (Exception e) {

            showAlert(Alert.AlertType.ERROR, "Order Failed", "Something went wrong while placing your order.");

            e.printStackTrace();
        }
    }

    private boolean showOrderSummary() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Order Summary");
        dialog.setHeaderText("Review Your Order");

        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20));

        StringBuilder summary = new StringBuilder();
        summary.append("Order Items:\n");
        summary.append("-".repeat(50)).append("\n");
        for (com.group16.grocery_app.model.CartItem item : cart.getItems()) {
            summary.append(String.format("%s: %.2f kg × ₺%.2f = ₺%.2f\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getEffectivePrice(),
                    item.getTotalPrice()));
        }
        summary.append("-".repeat(50)).append("\n");

        double subtotal = cart.getSubtotal();
        double couponDiscount = cart.getCouponDiscount();
        double loyaltyDiscount = cart.getLoyaltyDiscount();
        double afterDiscounts = cart.getTotalAfterDiscounts();
        double vat = afterDiscounts * VAT_RATE;
        double grandTotal = afterDiscounts + vat;

        summary.append(String.format("Subtotal: ₺%.2f\n", subtotal));
        if (couponDiscount > 0) {
            summary.append(String.format("Coupon Discount: -₺%.2f\n", couponDiscount));
        }
        if (loyaltyDiscount > 0) {
            summary.append(String.format("Loyalty Discount: -₺%.2f\n", loyaltyDiscount));
        }
        summary.append(String.format("After Discounts: ₺%.2f\n", afterDiscounts));
        summary.append(String.format("VAT (18%%): ₺%.2f\n", vat));
        summary.append(String.format("Grand Total: ₺%.2f\n", grandTotal));

        TextArea summaryArea = new TextArea(summary.toString());
        summaryArea.setEditable(false);
        summaryArea.setPrefRowCount(15);
        summaryArea.setPrefColumnCount(50);
        summaryArea.setWrapText(false);
        summaryArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        content.getChildren().add(summaryArea);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/appStyles.css").toExternalForm()
        );

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private LocalDateTime showDeliveryDateTimeDialog() {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Select Delivery Date & Time");
        dialog.setHeaderText("Please select delivery date and time (within 48 hours)");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(1));
        datePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusDays(2);
                setDisable(empty || date.isBefore(today.plusDays(1)) || date.isAfter(maxDate));
            }
        });

        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 10);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Date:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        HBox timeBox = new HBox(5);
        timeBox.getChildren().addAll(hourSpinner, new Label(":"), minuteSpinner);
        grid.add(timeBox, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/app.css").toExternalForm()
        );

        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        javafx.scene.control.Button confirmButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                showAlert(Alert.AlertType.WARNING, "Invalid Date", "Please select a delivery date.");
                event.consume();
                return;
            }

            LocalTime selectedTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime maxDateTime = now.plusHours(48);

            if (selectedDateTime.isBefore(now)) {
                showAlert(Alert.AlertType.WARNING, "Invalid Time", "Delivery time must be in the future.");
                event.consume();
                return;
            }
            if (selectedDateTime.isAfter(maxDateTime)) {
                showAlert(Alert.AlertType.WARNING, "Invalid Time", "Delivery must be within 48 hours.");
                event.consume();
                return;
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                LocalDate selectedDate = datePicker.getValue();
                if (selectedDate != null) {
                    LocalTime selectedTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
                    return LocalDateTime.of(selectedDate, selectedTime);
                }
            }
            return null;
        });

        Optional<LocalDateTime> result = dialog.showAndWait();
        return result.orElse(null);
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

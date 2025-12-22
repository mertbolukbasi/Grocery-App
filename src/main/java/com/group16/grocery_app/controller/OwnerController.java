package com.group16.grocery_app.controller;

import com.group16.grocery_app.HelloApplication;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class OwnerController {

    private static final String NAV_ACTIVE = "owner-nav-btn-active";
    private static final String NAV_NORMAL = "owner-nav-btn";

    // Root / Containers
    @FXML private BorderPane ownerRoot;
    @FXML private StackPane ownerContentStack;

    // Topbar
    @FXML private Label ownerLblAppTitle;
    @FXML private Label ownerLblUsername;
    @FXML private MFXButton ownerBtnLogout;

    // Nav buttons
    @FXML private MFXButton ownerBtnDashboard;
    @FXML private MFXButton ownerBtnProducts;
    @FXML private MFXButton ownerBtnCarriers;
    @FXML private MFXButton ownerBtnOrders;
    @FXML private MFXButton ownerBtnMessages;
    @FXML private MFXButton ownerBtnCoupons;
    @FXML private MFXButton ownerBtnLoyalty;
    @FXML private MFXButton ownerBtnRatings;
    @FXML private MFXButton ownerBtnReports;

    // Pages
    @FXML private AnchorPane ownerPaneDashboard;
    @FXML private AnchorPane ownerPaneProducts;
    @FXML private AnchorPane ownerPaneCarriers;
    @FXML private AnchorPane ownerPaneOrders;
    @FXML private AnchorPane ownerPaneMessages;
    @FXML private AnchorPane ownerPaneCoupons;
    @FXML private AnchorPane ownerPaneLoyalty;
    @FXML private AnchorPane ownerPaneRatings;
    @FXML private AnchorPane ownerPaneReports;

    // Dashboard
    @FXML private Label ownerLblTotalProductsValue;
    @FXML private Label ownerLblLowStockValue;
    @FXML private Label ownerLblPendingOrdersValue;
    @FXML private TabPane ownerTabDashboard;

    @FXML private TableView<?> ownerTblLowStock;
    @FXML private TableColumn<?, ?> ownerColLowStockName;
    @FXML private TableColumn<?, ?> ownerColLowStockType;
    @FXML private TableColumn<?, ?> ownerColLowStockStock;
    @FXML private TableColumn<?, ?> ownerColLowStockThreshold;

    @FXML private TableView<?> ownerTblPendingOrders;
    @FXML private TableColumn<?, ?> ownerColPendingOrderId;
    @FXML private TableColumn<?, ?> ownerColPendingCustomer;
    @FXML private TableColumn<?, ?> ownerColPendingAddress;
    @FXML private TableColumn<?, ?> ownerColPendingDate;
    @FXML private TableColumn<?, ?> ownerColPendingStatus;

    // Products
    @FXML private MFXTextField ownerTfProductSearch;
    @FXML private MFXButton ownerBtnProductClearSearch;
    @FXML private TableView<?> ownerTblProducts;
    @FXML private TableColumn<?, ?> ownerColProductName;
    @FXML private TableColumn<?, ?> ownerColProductType;
    @FXML private TableColumn<?, ?> ownerColProductPrice;
    @FXML private TableColumn<?, ?> ownerColProductStock;
    @FXML private TableColumn<?, ?> ownerColProductThreshold;

    @FXML private MFXTextField ownerTfProductName;
    @FXML private MFXComboBox<?> ownerCbProductType;
    @FXML private MFXTextField ownerTfProductPrice;
    @FXML private MFXTextField ownerTfProductStock;
    @FXML private MFXTextField ownerTfProductThreshold;
    @FXML private MFXButton ownerBtnProductUpload;
    @FXML private MFXButton ownerBtnProductImageView;
    @FXML private ImageView ownerImgProduct;
    @FXML private MFXButton ownerBtnProductSave;
    @FXML private MFXButton ownerBtnProductDelete;

    // Carriers
    @FXML private MFXTextField ownerTfCarrierSearch;
    @FXML private MFXButton ownerBtnCarrierClearSearch;
    @FXML private TableView<?> ownerTblCarriers;
    @FXML private TableColumn<?, ?> ownerColCarrierUsername;
    @FXML private TableColumn<?, ?> ownerColCarrierFullName;
    @FXML private TableColumn<?, ?> ownerColCarrierPhone;
    @FXML private TableColumn<?, ?> ownerColCarrierVehicle;
    @FXML private TableColumn<?, ?> ownerColCarrierStatus;

    @FXML private MFXTextField ownerTfCarrierUsername;
    @FXML private MFXTextField ownerTfCarrierFullName;
    @FXML private MFXTextField ownerTfCarrierPhone;
    @FXML private MFXComboBox<?> ownerCbCarrierVehicle;
    @FXML private MFXComboBox<?> ownerCbCarrierStatus;
    @FXML private MFXButton ownerBtnCarrierEmploy;
    @FXML private MFXButton ownerBtnCarrierFire;

    // Orders
    @FXML private MFXTextField ownerTfOrderSearch;
    @FXML private MFXButton ownerBtnOrderClearSearch;
    @FXML private MFXComboBox<?> ownerCbOrderStatus;
    @FXML private MFXDatePicker ownerDpOrderFrom;
    @FXML private MFXDatePicker ownerDpOrderTo;

    @FXML private TableView<?> ownerTblOrders;
    @FXML private TableColumn<?, ?> ownerColOrderId;
    @FXML private TableColumn<?, ?> ownerColOrderCustomer;
    @FXML private TableColumn<?, ?> ownerColOrderAddress;
    @FXML private TableColumn<?, ?> ownerColOrderTotal;
    @FXML private TableColumn<?, ?> ownerColOrderRequestedDate;
    @FXML private TableColumn<?, ?> ownerColOrderDeliveryDate;
    @FXML private TableColumn<?, ?> ownerColOrderStatus;

    // Messages
    @FXML private MFXTextField ownerTfMessageSearch;
    @FXML private MFXButton ownerBtnMessageClearSearch;
    @FXML private MFXComboBox<?> ownerCbMessageStatus;
    @FXML private MFXDatePicker ownerDpMessageFrom;
    @FXML private MFXDatePicker ownerDpMessageTo;

    @FXML private TableView<?> ownerTblMessages;
    @FXML private TableColumn<?, ?> ownerColMessageId;
    @FXML private TableColumn<?, ?> ownerColMessageCustomer;
    @FXML private TableColumn<?, ?> ownerColMessageSubject;
    @FXML private TableColumn<?, ?> ownerColMessageDate;
    @FXML private TableColumn<?, ?> ownerColMessageStatus;

    @FXML private Label ownerLblMsgFromValue;
    @FXML private Label ownerLblMsgSubjectValue;
    @FXML private Label ownerLblMsgDateValue;
    @FXML private TextArea ownerTaMessageBody;
    @FXML private TextArea ownerTaReply;
    @FXML private MFXButton ownerBtnSendReply;

    // Coupons
    @FXML private MFXTextField ownerTfCouponSearch;
    @FXML private MFXButton ownerBtnCouponClearSearch;
    @FXML private MFXComboBox<?> ownerCbCouponStatusFilter;

    @FXML private TableView<?> ownerTblCoupons;
    @FXML private TableColumn<?, ?> ownerColCouponCode;
    @FXML private TableColumn<?, ?> ownerColCouponType;
    @FXML private TableColumn<?, ?> ownerColCouponValue;
    @FXML private TableColumn<?, ?> ownerColCouponStatus;

    @FXML private MFXTextField ownerTfCouponCode;
    @FXML private MFXComboBox<?> ownerCbCouponType;
    @FXML private MFXTextField ownerTfCouponValue;
    @FXML private MFXComboBox<?> ownerCbCouponStatus;
    @FXML private MFXButton ownerBtnCouponSave;
    @FXML private MFXButton ownerBtnCouponDelete;

    // Loyalty
    @FXML private MFXTextField ownerTfLoyaltySearch;
    @FXML private MFXButton ownerBtnLoyaltyClearSearch;

    @FXML private TableView<?> ownerTblLoyalty;
    @FXML private TableColumn<?, ?> ownerColLoyaltyCustomer;
    @FXML private TableColumn<?, ?> ownerColLoyaltyPoints;
    @FXML private TableColumn<?, ?> ownerColLoyaltyTier;

    @FXML private MFXTextField ownerTfLoyaltyCustomer;
    @FXML private MFXTextField ownerTfLoyaltyCurrentPoints;
    @FXML private MFXComboBox<?> ownerCbLoyaltyAction;
    @FXML private MFXTextField ownerTfLoyaltyAmount;
    @FXML private MFXButton ownerBtnLoyaltySave;
    @FXML private MFXButton ownerBtnLoyaltyDelete;

    // Ratings
    @FXML private MFXTextField ownerTfRatingSearch;
    @FXML private MFXButton ownerBtnRatingClearSearch;
    @FXML private MFXComboBox<?> ownerCbMinScore;

    @FXML private TableView<?> ownerTblRatings;
    @FXML private TableColumn<?, ?> ownerColRatingCustomer;
    @FXML private TableColumn<?, ?> ownerColRatingTarget;
    @FXML private TableColumn<?, ?> ownerColRatingScore;
    @FXML private TableColumn<?, ?> ownerColRatingDate;
    @FXML private TableColumn<?, ?> ownerColRatingStatus;

    @FXML private Label ownerLblRatingCustomerValue;
    @FXML private Label ownerLblRatingTargetValue;
    @FXML private Label ownerLblRatingScoreValue;
    @FXML private Label ownerLblRatingDateValue;
    @FXML private Label ownerLblRatingStatusValue;

    // Reports
    @FXML private MFXTextField ownerTfReportSearch;
    @FXML private MFXButton ownerBtnReportClearSearch;
    @FXML private MFXComboBox<?> ownerCbReportType;
    @FXML private MFXDatePicker ownerDpReportFrom;
    @FXML private MFXDatePicker ownerDpReportTo;
    @FXML private MFXButton ownerBtnReportGenerate;

    @FXML private TableView<?> ownerTblReports;
    @FXML private TableColumn<?, ?> ownerColReportName;
    @FXML private TableColumn<?, ?> ownerColReportValue;
    @FXML private TableColumn<?, ?> ownerColReportDate;

    @FXML private BarChart<String, Number> ownerChartReport;
    @FXML private CategoryAxis ownerAxisReportDate;
    @FXML private NumberAxis ownerAxisReportValue;

    @FXML private Label ownerLblReportTypeValue;
    @FXML private Label ownerLblReportDateRangeValue;
    @FXML private Label ownerLblReportTotalRecordsValue;
    @FXML private Label ownerLblReportTotalAmountValue;

    private List<AnchorPane> pages;
    private List<MFXButton> navButtons;

    @FXML
    public void initialize() {
        pages = List.of(
                ownerPaneDashboard,
                ownerPaneProducts,
                ownerPaneCarriers,
                ownerPaneOrders,
                ownerPaneMessages,
                ownerPaneCoupons,
                ownerPaneLoyalty,
                ownerPaneRatings,
                ownerPaneReports
        );

        navButtons = List.of(
                ownerBtnDashboard,
                ownerBtnProducts,
                ownerBtnCarriers,
                ownerBtnOrders,
                ownerBtnMessages,
                ownerBtnCoupons,
                ownerBtnLoyalty,
                ownerBtnRatings,
                ownerBtnReports
        );

        ownerBtnDashboard.setOnAction(e -> openPage(ownerBtnDashboard, ownerPaneDashboard));
        ownerBtnProducts.setOnAction(e -> openPage(ownerBtnProducts, ownerPaneProducts));
        ownerBtnCarriers.setOnAction(e -> openPage(ownerBtnCarriers, ownerPaneCarriers));
        ownerBtnOrders.setOnAction(e -> openPage(ownerBtnOrders, ownerPaneOrders));
        ownerBtnMessages.setOnAction(e -> openPage(ownerBtnMessages, ownerPaneMessages));
        ownerBtnCoupons.setOnAction(e -> openPage(ownerBtnCoupons, ownerPaneCoupons));
        ownerBtnLoyalty.setOnAction(e -> openPage(ownerBtnLoyalty, ownerPaneLoyalty));
        ownerBtnRatings.setOnAction(e -> openPage(ownerBtnRatings, ownerPaneRatings));
        ownerBtnReports.setOnAction(e -> openPage(ownerBtnReports, ownerPaneReports));

        ownerBtnLogout.setOnAction(e -> logoutToLogin());

        ownerBtnProductClearSearch.setOnAction(e -> ownerTfProductSearch.clear());
        ownerBtnCarrierClearSearch.setOnAction(e -> ownerTfCarrierSearch.clear());
        ownerBtnOrderClearSearch.setOnAction(e -> ownerTfOrderSearch.clear());
        ownerBtnMessageClearSearch.setOnAction(e -> ownerTfMessageSearch.clear());
        ownerBtnCouponClearSearch.setOnAction(e -> ownerTfCouponSearch.clear());
        ownerBtnLoyaltyClearSearch.setOnAction(e -> ownerTfLoyaltySearch.clear());
        ownerBtnRatingClearSearch.setOnAction(e -> ownerTfRatingSearch.clear());
        ownerBtnReportClearSearch.setOnAction(e -> ownerTfReportSearch.clear());

        showPage(ownerPaneDashboard);
        setActiveNavButton(ownerBtnDashboard);
    }

    private void openPage(MFXButton navBtn, AnchorPane targetPage) {
        setActiveNavButton(navBtn);
        showPage(targetPage);
    }

    private void showPage(AnchorPane target) {
        for (AnchorPane p : pages) {
            if (p == null) continue;
            p.setVisible(false);
            p.setManaged(false);
        }
        if (target != null) {
            target.setVisible(true);
            target.setManaged(true);
            target.toFront();
        }
    }

    private void setActiveNavButton(MFXButton activeButton) {
        for (MFXButton b : navButtons) {
            if (b == null) continue;
            b.getStyleClass().remove(NAV_ACTIVE);
            if (!b.getStyleClass().contains(NAV_NORMAL)) {
                b.getStyleClass().add(NAV_NORMAL);
            }
        }
        if (activeButton != null) {
            activeButton.getStyleClass().remove(NAV_NORMAL);
            if (!activeButton.getStyleClass().contains(NAV_ACTIVE)) {
                activeButton.getStyleClass().add(NAV_ACTIVE);
            }
        }
    }

    private void logoutToLogin() {
        Stage stage = (Stage) ownerBtnLogout.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("LoginView.fxml"));
            Scene scene = new Scene(loader.load(), 960, 540);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "LoginView.fxml could not be loaded.");
        }
    }

    public void setOwnerUsername(String username) {
        ownerLblUsername.setText(username);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

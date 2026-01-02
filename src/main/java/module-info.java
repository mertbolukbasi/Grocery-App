module com.group16.grocery_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires MaterialFX;
    requires org.controlsfx.controls;
    requires org.apache.pdfbox;
    requires java.desktop;

    opens com.group16.grocery_app.controller to javafx.fxml;
    opens com.group16.grocery_app.model to javafx.base;
    opens com.group16.grocery_app to javafx.fxml;
    exports com.group16.grocery_app;
}
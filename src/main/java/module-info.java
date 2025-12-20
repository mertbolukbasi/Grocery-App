module com.group16.grocery_app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires MaterialFX;
    requires org.controlsfx.controls;

    opens com.group16.grocery_app to javafx.fxml;

    opens com.group16.grocery_app.controller to javafx.fxml;
    opens com.group16.grocery_app.model to javafx.base;

    exports com.group16.grocery_app;
}

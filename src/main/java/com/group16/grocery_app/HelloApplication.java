package com.group16.grocery_app;

import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        UserAgentBuilder.builder()
                .themes(MaterialFXStylesheets.forAssemble(true))
                .setDeploy(true)
                .setResolveAssets(true)
                .build()
                .setGlobal();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("CarrierView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("carrierStyle.css")).toExternalForm());
        stage.setTitle("Hello!");
        //stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}

package com.group16.grocery_app.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxcore.controls.Label;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

public class LoginController {

    private final ValidationSupport validation = new ValidationSupport();

    @FXML
    private MFXTextField confirmPasswordInput;

    @FXML
    private MFXTextField firstNameInput;

    @FXML
    private Label helloLabel;

    @FXML
    private MFXTextField lastNameInput;

    @FXML
    private VBox leftVBox;

    @FXML
    private MFXTextField passwordInput;

    @FXML
    private MFXTextField passwordInputReg;

    @FXML
    private VBox rightVBox;

    @FXML
    private VBox rootPane;

    @FXML
    private MFXButton signUpButtonReg;

    @FXML
    private Label signInLabel;

    @FXML
    private MFXButton signInLog;

    @FXML
    private Label signUpLabel;

    @FXML
    private MFXButton tempButton;

    @FXML
    private MFXTextField usernameInput;

    @FXML
    private MFXTextField usernameInputReg;

    @FXML
    private VBox wrapVBox;

    private boolean isSignUp = false;

    @FXML
    public void initialize() {
        validation.registerValidator(usernameInputReg, createLetterValidator("Username"));
        validation.registerValidator(firstNameInput, createLetterValidator("First Name"));
        validation.registerValidator(lastNameInput, createLetterValidator("Last Name"));

        validation.registerValidator(passwordInputReg, false, (Control c, String newValue) ->
                ValidationResult.fromErrorIf(c, "Password must be at least 8 characters long.",
                        newValue == null || newValue.trim().length() < 8));

        validation.registerValidator(confirmPasswordInput, false, (Control c, String newValue) -> {
            String password = passwordInputReg.getText();
            boolean match = newValue != null && newValue.equals(password);
            return ValidationResult.fromErrorIf(c, "Passwords do not match.", !match);
        });

        signUpButtonReg.disableProperty().bind(validation.invalidProperty());

        tempButton.setOnAction(event -> {
            setButtonsDisabled(true);
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.8), wrapVBox);

            if (isSignUp) {
                slide.setToX(0);
                isSignUp = false;
                helloLabel.setText("Hello, Friend!");
                tempButton.setText("Sign Up");
            } else {
                double slideDistance = (wrapVBox.getWidth() > 0) ? wrapVBox.getWidth() : 380;
                slide.setToX(-slideDistance);
                isSignUp = true;
                helloLabel.setText("Welcome Back!");
                tempButton.setText("Sign In");
            }

            slide.setOnFinished(e -> {
                setButtonsDisabled(false);
            });

            slide.play();
        });
    }

    private Validator<String> createLetterValidator(String fieldName) {
        return (Control c, String value) -> {
            ValidationResult result = new ValidationResult();

            if (value == null || value.trim().length() < 2) {
                result.addErrorIf(c, fieldName + " must be at least 2 characters.", true);
            }

            if (value != null && !value.matches("^[a-zA-Z]+$")) {
                result.addErrorIf(c, fieldName + " must contain only letters.", true);
            }

            return result;
        };
    }

    private void setButtonsDisabled(boolean disabled) {
        tempButton.setDisable(disabled);
        signInLog.setDisable(disabled);
    }
}

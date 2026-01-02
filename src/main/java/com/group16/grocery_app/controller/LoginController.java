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
import com.group16.grocery_app.db.service.UserService;
import com.group16.grocery_app.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;

/**
 * Handles user login and registration functionality.
 * Manages form validation and navigation based on user roles.
 *
 * @author Mert Bölükbaşı
 */
public class LoginController {

    private final ValidationSupport validation = new ValidationSupport();
    private boolean isCheckingUsername = false;

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

    private UserService userService = new UserService();
    private User loggedInUser;

    /**
     * Initializes the controller when FXML is loaded.
     * Sets up form validators and event handlers.
     *
     * @author Mert Bölükbaşı
     */
    @FXML
    public void initialize() {
        validation.registerValidator(usernameInputReg, createUsernameValidator("Username"));

        setupUsernameUniquenessCheck();

        validation.registerValidator(firstNameInput, createLetterValidator("First Name"));
        validation.registerValidator(lastNameInput, createLetterValidator("Last Name"));

        validation.registerValidator(passwordInputReg, false, createStrongPasswordValidator());

        validation.registerValidator(confirmPasswordInput, false, (Control c, String newValue) -> {
            String password = passwordInputReg.getText();
            boolean match = newValue != null && newValue.equals(password);
            return ValidationResult.fromErrorIf(c, "Passwords do not match.", !match);
        });

        signUpButtonReg.disableProperty().bind(validation.invalidProperty());

        signInLog.setOnAction(event -> handleSignIn());
        signUpButtonReg.setOnAction(event -> handleSignUp());

        tempButton.setOnAction(event -> {
            setButtonsDisabled(true);
            TranslateTransition slide = new TranslateTransition(Duration.seconds(0.8), wrapVBox);

            if (isSignUp) {
                slide.setToX(0);
                isSignUp = false;
                helloLabel.setText("Hello!");
                tempButton.setText("Sign Up");
            } else {
                double slideDistance = (wrapVBox.getWidth() > 0) ? wrapVBox.getWidth() : 380;
                slide.setToX(-slideDistance);
                isSignUp = true;
                helloLabel.setText("Welcome!");
                tempButton.setText("Sign In");
            }

            slide.setOnFinished(e -> {
                setButtonsDisabled(false);
            });

            slide.play();
        });
    }

    /**
     * Creates a validator for name fields that only accepts letters.
     *
     * @param fieldName the field name to display in validation messages
     * @return validator that accepts values with only letters and at least 2 characters
     * @author Mert Bölükbaşı
     */
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

    /**
     * Creates a validator for username format validation.
     *
     * @param fieldName the field name to display in validation messages
     * @return validator that accepts usernames with letters, numbers, underscores, and at least 2 characters
     * @author Mert Bölükbaşı
     */
    private Validator<String> createUsernameValidator(String fieldName) {
        return (Control c, String value) -> {
            ValidationResult result = new ValidationResult();

            if (value == null || value.trim().length() < 2) {
                result.addErrorIf(c, fieldName + " must be at least 2 characters.", true);
            }

            if (value != null && !value.matches("^[a-zA-Z0-9_]+$")) {
                result.addErrorIf(c, fieldName + " must contain only letters, numbers, and underscores.", true);
            }

            return result;
        };
    }

    /**
     * Creates a validator that enforces strong password requirements.
     *
     * @return validator that checks for 8+ chars, uppercase, lowercase, digit, and special character
     * @author Mert Bölükbaşı
     */
    private Validator<String> createStrongPasswordValidator() {
        return (Control c, String newValue) -> {
            ValidationResult result = new ValidationResult();

            if (newValue == null || newValue.trim().isEmpty()) {
                result.addErrorIf(c, "Password is required.", true);
                return result;
            }

            String password = newValue;
            StringBuilder errorMessages = new StringBuilder();

            if (password.length() < 8) {
                errorMessages.append("Password must be at least 8 characters long. ");
            }

            if (!password.matches(".*[A-Z].*")) {
                errorMessages.append("Must contain at least one uppercase letter. ");
            }

            if (!password.matches(".*[a-z].*")) {
                errorMessages.append("Must contain at least one lowercase letter. ");
            }

            if (!password.matches(".*[0-9].*")) {
                errorMessages.append("Must contain at least one digit. ");
            }

            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
                errorMessages.append("Must contain at least one special character (!@#$%^&*...). ");
            }

            if (errorMessages.length() > 0) {
                result.addErrorIf(c, errorMessages.toString().trim(), true);
            }

            return result;
        };
    }

    /**
     * Sets up async username uniqueness check with debounce.
     * Checks database after user stops typing for 500ms.
     *
     * @author Mert Bölükbaşı
     */
    private void setupUsernameUniquenessCheck() {
        usernameInputReg.textProperty().addListener(new ChangeListener<String>() {
            private Task<Void> delayTask = null;
            private javafx.concurrent.Service<Void> delayService = null;

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (delayService != null && delayService.isRunning()) {
                    delayService.cancel();
                }

                String username = newValue != null ? newValue.trim() : "";

                usernameInputReg.getStyleClass().removeAll("validation-error", "validation-success");

                if (username.length() < 2 || !username.matches("^[a-zA-Z0-9_]+$")) {
                    return;
                }

                delayTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(500);
                        return null;
                    }
                };

                delayService = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return delayTask;
                    }
                };

                delayTask.setOnSucceeded(e -> {
                    if (!delayTask.isCancelled()) {
                        String currentUsername = usernameInputReg.getText().trim();
                        if (currentUsername.equals(username) && username.length() >= 2
                                && username.matches("^[a-zA-Z0-9_]+$")) {
                            checkUsernameAvailability(username);
                        }
                    }
                });

                delayService.start();
            }
        });
    }

    /**
     * Checks if username exists in database and updates UI style accordingly.
     *
     * @param username the username to check for availability
     * @author Mert Bölükbaşı
     */
    private void checkUsernameAvailability(String username) {
        if (isCheckingUsername) {
            return;
        }

        isCheckingUsername = true;
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userService.usernameExists(username);
            }
        };

        task.setOnSucceeded(e -> {
            isCheckingUsername = false;
            if (task.isCancelled()) {
                return;
            }

            boolean exists = task.getValue();
            String currentUsername = usernameInputReg.getText().trim();

            if (currentUsername.equals(username)) {
                Platform.runLater(() -> {
                    if (exists) {
                        usernameInputReg.getStyleClass().remove("validation-success");
                        if (!usernameInputReg.getStyleClass().contains("validation-error")) {
                            usernameInputReg.getStyleClass().add("validation-error");
                        }
                    } else {
                        usernameInputReg.getStyleClass().remove("validation-error");
                        if (!usernameInputReg.getStyleClass().contains("validation-success")) {
                            usernameInputReg.getStyleClass().add("validation-success");
                        }
                    }
                });
            }
        });

        task.setOnFailed(e -> {
            isCheckingUsername = false;
            System.err.println("Error checking username availability: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /**
     * Enables or disables the sign in and toggle buttons.
     *
     * @param disabled true to disable buttons, false to enable
     * @author Mert Bölükbaşı
     */
    private void setButtonsDisabled(boolean disabled) {
        tempButton.setDisable(disabled);
        signInLog.setDisable(disabled);
    }

    /**
     * Handles user sign in attempt.
     * Validates credentials and navigates to appropriate view based on user role.
     *
     * @author Mert Bölükbaşı
     */
    @FXML
    private void handleSignIn() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();

        if (username == null || username.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter your username.");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please enter your password.");
            return;
        }

        try {
            loggedInUser = userService.login(username.trim(), password);
            System.out.println(username + password);

            if (loggedInUser != null) {
                com.group16.grocery_app.model.Role role = loggedInUser.getRole();
                if (role == com.group16.grocery_app.model.Role.CUSTOMER) {
                    navigateToCustomerView();
                } else if (role == com.group16.grocery_app.model.Role.OWNER) {
                    navigateToOwnerView();
                } else if (role == com.group16.grocery_app.model.Role.CARRIER) {
                    navigateToCarrierView();
                } else {
                    showAlert(Alert.AlertType.WARNING, "Access Denied", "Unknown user role.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed",
                        "Invalid username or password.\n\n" +
                                "Note: If you're using default accounts, try:\n" +
                                "Customer: cust/cust\n" +
                                "Owner: own/own\n" +
                                "Carrier: carr/carr\n" +
                                "Or register a new customer account.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Login Error",
                    "An error occurred during login. Please check:\n" +
                            "1. Database connection is active\n" +
                            "2. Database is properly set up\n" +
                            "Error: " + e.getMessage());
        }
    }

    /**
     * Handles new user registration.
     * Validates all fields and creates account if valid.
     *
     * @author Mert Bölükbaşı
     */
    @FXML
    private void handleSignUp() {
        String username = usernameInputReg.getText();
        String firstName = firstNameInput.getText();
        String lastName = lastNameInput.getText();
        String password = passwordInputReg.getText();
        String confirmPassword = confirmPasswordInput.getText();

        if (username == null || username.trim().isEmpty() ||
                firstName == null || firstName.trim().isEmpty() ||
                lastName == null || lastName.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match.");
            return;
        }

        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Invalid Password", "Password must be at least 8 characters long.");
            return;
        }

        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") ||
                !password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            showAlert(Alert.AlertType.WARNING, "Weak Password",
                    "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
            return;
        }

        try {
            if (userService.usernameExists(username.trim())) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "The username already exists. Please choose a different username.");
                return;
            }

            boolean success = userService.register(username.trim(), password, firstName.trim(), lastName.trim());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                        "Your account has been created successfully. Please sign in.");
                usernameInputReg.clear();
                firstNameInput.clear();
                lastNameInput.clear();
                passwordInputReg.clear();
                confirmPasswordInput.clear();
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed",
                        "Registration failed. The username may already exist. Please choose a different username and try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Error",
                    "An error occurred during registration. Please check your input and try again.\nError: "
                            + e.getMessage());
        }
    }

    /**
     * Navigates to the customer view after successful login.
     *
     * @author Mert Bölükbaşı
     */
    private void navigateToCustomerView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/customer.fxml"));
            Parent root = loader.load();

            CustomerController controller = loader.getController();
            controller.setCurrentUser(loggedInUser);
            controller.setUsername(loggedInUser.getUsername());

            Stage stage = (Stage) signInLog.getScene().getWindow();
            Scene scene = new Scene(root, 960, 540);
            stage.setScene(scene);
            stage.setTitle("Group16 GreenGrocer - Customer");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load customer view.");
        }
    }

    /**
     * Navigates to the owner view after successful login.
     *
     * @author Mert Bölükbaşı
     */
    private void navigateToOwnerView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/owner.fxml"));
            Parent root = loader.load();
            OwnerController controller = loader.getController();
            controller.setCurrentUser(loggedInUser);
            Stage stage = (Stage) signInLog.getScene().getWindow();
            Scene scene = new Scene(root, 960, 540);
            stage.setScene(scene);
            stage.setTitle("Group16 GreenGrocer - Owner");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load owner view.");
        }
    }

    /**
     * Navigates to the carrier view after successful login.
     *
     * @author Mert Bölükbaşı
     */
    private void navigateToCarrierView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/carrier.fxml"));
            Parent root = loader.load();
            CarrierController controller = loader.getController();
            controller.setCurrentUser(loggedInUser);
            Stage stage = (Stage) signInLog.getScene().getWindow();
            Scene scene = new Scene(root, 960, 540);
            stage.setScene(scene);
            stage.setTitle("Group16 GreenGrocer - Carrier");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load carrier view.");
        }
    }

    /**
     * Shows an alert dialog with the specified type, title, and message.
     *
     * @param type the alert type (warning, error, information, etc.)
     * @param title the alert title
     * @param message the alert message content
     * @author Mert Bölükbaşı
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

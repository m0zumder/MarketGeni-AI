package com.aimarketing.controllers;

import com.aimarketing.models.User;
import com.aimarketing.services.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

public class LoginController {
    private final AuthenticationService authService = new AuthenticationService();

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<User.Role> roleComboBox;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll(User.Role.values());
        roleComboBox.setValue(User.Role.CLIENT);
    }

    @FXML
    private void handleLogin() {
        try {
            String token = authService.login(usernameField.getText(), passwordField.getText());
            User user = authService.getCurrentUser(token);
            navigateToMainView(user);
        } catch (IllegalArgumentException e) {
            showError("Login Failed", e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            User user = authService.register(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                roleComboBox.getValue()
            );
            showInfo("Registration Successful", "Please login with your credentials");
            clearFields();
        } catch (IllegalArgumentException e) {
            showError("Registration Failed", e.getMessage());
        }
    }

    private void navigateToMainView(User user) {
        // TODO: Implement navigation to main view based on user role
        if (user.getRole() == User.Role.ADMIN) {
            // Navigate to admin dashboard
        } else {
            // Navigate to client dashboard
        }
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(User.Role.CLIENT);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
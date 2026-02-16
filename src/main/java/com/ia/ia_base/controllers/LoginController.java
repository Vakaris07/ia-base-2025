package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.UserDAO;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
//import com.ia.ia_base.util.PasswordHasher;
//import com.ia.ia_base.util.SessionManager;
import com.ia.ia_base.util.PasswordHasher;
import com.ia.ia_base.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController extends BaseController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Hyperlink registerLink;

    private UserDAO userDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            AlertManager.showError("Error", "Please enter email and password");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);

            if (user == null) {
                AlertManager.showError("Login Failed", "Invalid email or password");
                return;
            }

            if (user.isBlocked()) {
                AlertManager.showError("Account Blocked", "Your account has been blocked. Please contact administrator.");
                return;
            }

            if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
                AlertManager.showError("Login Failed", "Invalid email or password");
                return;
            }

            // Login successful
            SessionManager.getInstance().setCurrentUser(user);

            // Check if password must be changed
            if (user.isMustChangePassword()) {
                AlertManager.showWarning("Change Password", "You must change your password on first login.");
                // TODO: Open password change dialog
            }

            // Open main window
            changeScene("views/MainView.fxml");
            if (stage != null) {
                stage.setTitle("Device Inventory Management Application");
                stage.setResizable(true);
                stage.setWidth(1000);
                stage.setHeight(625);
            }

        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterLink() {
        changeScene("views/internal_views/register-view.fxml");
        if (stage != null) {
            stage.setTitle("Device Inventory Management Application - Registration");
        }
    }
}

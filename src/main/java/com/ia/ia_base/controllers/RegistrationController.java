package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.RoleDAO;
import com.ia.ia_base.database.DAO.UserDAO;
import com.ia.ia_base.models.RegisteredUser;
import com.ia.ia_base.models.Role;
import com.ia.ia_base.models.User;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.PasswordHasher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegistrationController extends BaseController {

    public static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    public TextField emailField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Button registerButton;
    public Hyperlink loginLink;

    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        userDAO = new UserDAO();
        roleDAO = new RoleDAO();

    }

    public void handleRegister() throws SQLException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();


        // Validate credentials
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            AlertManager.showError("Error","Please fill out all fields");
            return;
        }

        if(!EMAIL_PATTERN.matcher(email).matches()){
            AlertManager.showError("Error", "Invalid email address");
            return;
        }
        if(password.length()<6){
            AlertManager.showError("Weak password","Password must be at least 6 characters");
            return;
        }
        if(!password.equals(confirmPassword)){
            AlertManager.showError("Password mismatch","Password and confirm password do not match");
            return;
        }
        Role role = roleDAO.findByName("registeredUser");

        try{
            // Check if email already exists
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                AlertManager.showError("Registration Failed", "Email already registered");
                return;
            }

            // Get "registered" role
            Role registeredRole = roleDAO.findByName("registeredUser");
            if (registeredRole == null) {
                AlertManager.showError("Error", "System error: registered role not found");
                return;
            }

            // Create new user
            // Use RegisteredUser class (inheritance)
            com.ia.ia_base.models.RegisteredUser newUser = new com.ia.ia_base.models.RegisteredUser();
            newUser.setEmail(email);
            newUser.setPasswordHash(PasswordHasher.hashPassword(password));
            newUser.setRole(registeredRole);
            newUser.setBlocked(false);
            newUser.setMustChangePassword(false);

            userDAO.create(newUser);

            AlertManager.showInfo("Success", "Registration successful! You can now login.");
            handleLoginLink();

        }catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to register: " + e.getMessage());
            e.printStackTrace();

        }


    }
    public void handleLoginLink() {
        changeScene("views/internal_views/old-login-view.fxml");
        if (stage != null) {
            stage.setTitle("IA Base Application - Log in");
        }
    }
}

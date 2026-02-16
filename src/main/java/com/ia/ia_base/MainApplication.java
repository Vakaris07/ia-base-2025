package com.ia.ia_base;

import com.ia.ia_base.config.AppConfig;
import com.ia.ia_base.controllers.LoginController;
import com.ia.ia_base.controllers.MainController;
import com.ia.ia_base.controllers.RegistrationController;
import com.ia.ia_base.database.DAO.RoleDAO;
import com.ia.ia_base.database.DAO.UserDAO;
import com.ia.ia_base.database.DatabaseConnection;
import com.ia.ia_base.models.AdminUser;
import com.ia.ia_base.models.Role;
import com.ia.ia_base.util.PasswordHasher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Main application class.
 * This class starts the JavaFX application.
 */
public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Try to connect to database only if it's enabled
        if (AppConfig.isUseDatabase()) {
            try {
                DatabaseConnection.getInstance().getConnection();
            } catch (SQLException e) {
                System.err.println("WARNING: Failed to connect to database!");
                System.err.println("Check settings in DatabaseConnection class.");
                System.err.println("Or disable DB usage with AppConfig.setUseDatabase(false)");
            }
        } else {
            System.out.println("Database not used. Application will run without DB.");
        }
        try {
            createAdminUserIfNotExist();
        } catch (SQLException e) {
        }

        // Load main window
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("views/internal_views/old-login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        // Set Stage object in controller

        LoginController controller = fxmlLoader.getController();
        if (controller != null) {
            controller.setStage(stage);
        }

        stage.setTitle("Device Inventory Management Application");
        stage.setScene(scene);
        stage.show();
    }

    private void createAdminUserIfNotExist() throws SQLException {
        UserDAO userDAO = new UserDAO();
        RoleDAO roleDAO = new RoleDAO();

        var existingAdmin = userDAO.findByEmail(AppConfig.ADMIN_EMAIL);

        if (existingAdmin != null) {
            return;
        }

        Role adminRole = roleDAO.findByName("admin");

        if (adminRole == null) {
            throw new SQLException("Admin role does not exist");
        }

        //Create admin user

        AdminUser admin = new AdminUser();
        admin.setEmail(AppConfig.ADMIN_EMAIL);
        admin.setPasswordHash(PasswordHasher.hashPassword(AppConfig.ADMIN_DEFAULT_PASSWORD));
        admin.setRole(adminRole);
        admin.setBlocked(false);
        admin.setMustChangePassword(true);

        userDAO.create(admin);

        System.out.println("Created admin user: " + AppConfig.ADMIN_EMAIL);
    }

    @Override
    public void stop() {
        // Close database connection before closing application
        DatabaseConnection.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch();
    }
}


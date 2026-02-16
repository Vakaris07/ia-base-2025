package com.ia.ia_base.controllers;

import com.ia.ia_base.util.AlertManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main window controller with menu system.
 */
public class MainController extends BaseController {


    @FXML
    private MenuBar menuBar;
    @FXML
    private BorderPane mainPane;

    @FXML
    private MenuController menuController;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        if (menuController != null) {
            menuController.setMainController(this);
        }
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);  // sets stage in BaseController for MainController

        // Forward the same stage to the included MenuController
        if (menuController != null) {
            menuController.setStage(stage);
        }
    }

    public void loadCenterView(String fxmlPath) {
        try {
            String fullPath = "/com/ia/ia_base/" + fxmlPath;
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(fullPath)
            );
            Parent root = loader.load();

            BaseController controller = loader.getController();
            if (controller != null) {
                controller.setStage(stage);
            }

            mainPane.setCenter(root);   //only change center
        } catch (Exception e) {
            System.err.println("Error loading center view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Confirms if user really wants to exit the application
     */
    private void confirmExit() {
        if (AlertManager.confirmExit()) {
            if (stage != null) {
                stage.close();
            }
        }
    }

    /**
     * Opens example window (used from button)
     * Replaces current window instead of opening new one
     */
    @FXML
    private void openMainView() {
        changeScene("views/MainView.fxml");
        if (stage != null) {
            stage.setTitle("Example Window");
        }
    }


    /**
     * Adds new menu item programmatically
     */
    public void addMenuItem(String menuName, String itemName, Runnable action) {
        Menu menu = menuBar.getMenus().stream()
                .filter(m -> m.getText().equals(menuName))
                .findFirst()
                .orElse(null);

        if (menu != null) {
            MenuItem menuItem = new MenuItem(itemName);
            menuItem.setOnAction(e -> action.run());
            menu.getItems().add(menuItem);
        }
    }
}


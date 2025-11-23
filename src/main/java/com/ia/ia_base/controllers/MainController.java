package com.ia.ia_base.controllers;

import com.ia.ia_base.util.AlertManager;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

/**
 * Main window controller with menu system.
 */
public class MainController extends BaseController {
    
    @FXML
    private BorderPane mainPane;
    
    @FXML
    private MenuBar menuBar;
    
    @FXML
    private Menu fileMenu;
    
    @FXML
    private MenuItem exitMenuItem;
    
    @FXML
    private Menu viewMenu;
    
    @FXML
    private MenuItem exampleViewMenuItem;
    
    @FXML
    private Menu helpMenu;
    
    @FXML
    private MenuItem aboutMenuItem;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupMenuActions();
    }
    
    /**
     * Sets up menu actions
     */
    private void setupMenuActions() {
        // Close application with confirmation
        exitMenuItem.setOnAction(e -> {
            confirmExit();
        });
        
        // Open example window (replaces current window)
        exampleViewMenuItem.setOnAction(e -> {
            changeScene("views/ExampleView.fxml");
            if (stage != null) {
                stage.setTitle("Example Window");
            }
        });
        
        // About menu
        aboutMenuItem.setOnAction(e -> {
            openModalWindow("views/AboutView.fxml", "About");
        });
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
    private void openExampleView() {
        changeScene("views/ExampleView.fxml");
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


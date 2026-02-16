package com.ia.ia_base.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.ia.ia_base.util.AlertManager.confirmExit;

public class MenuController extends BaseController {


    @FXML
    public Button logout_button;
    @FXML
    private AnchorPane inventoryView;

    private MainController mainController;


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void onLogout(){
       if (confirmExit()){
           changeScene("views/internal_views/old-login-view.fxml");
           if (stage != null) {
               stage.setTitle("Device Inventory Management Application");
           }
       }

    }

    @FXML
    private void openInventoryView() {
        if (mainController != null) {
            mainController.loadCenterView("views/internal_views/inventory-view.fxml");
        } else {
            System.err.println("mainController is null in MenuController");
        }
    }
    @FXML
    private void openReportsView(){
        if (mainController != null) {
            mainController.loadCenterView("views/internal_views/report-view.fxml");
        } else {
            System.err.println("mainController is null in MenuController");
        }
    }
    @FXML
    private void openHomePageView(){
        if (mainController != null) {
            mainController.loadCenterView("views/internal_views/home-page-view.fxml");
        } else {
            System.err.println("mainController is null in MenuController");
        }
    }









}

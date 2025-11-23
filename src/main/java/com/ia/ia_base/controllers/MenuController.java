package com.ia.ia_base.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends BaseController {

    public Pane menu_pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void openInventoryView() {
        changeScene("views/internal_views/inventory-view.fxml");
        if (stage != null) {
            stage.setTitle("Example Window");
        }
    }


}

package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.AttributeDAO;
import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.SQLException;

public class DeviceAttributesController extends BaseController {

    @FXML
    private Button cancel_button;
    @FXML
    private Button done_button;
    @FXML
    private TableView<AttributeDefinition> attributes_table_view;
    @FXML
    private Button new_attribute_button;
    @FXML
    private TableColumn<AttributeDefinition, Integer> id_column;
    @FXML
    private TableColumn<AttributeDefinition, String> name_column;
    @FXML
    private TableColumn<AttributeDefinition, String> data_type_column;
    @FXML
    private TableColumn<AttributeDefinition, String> description_column;
    @FXML
    private TableColumn<AttributeDefinition, Void> actions_column;

    private AttributeDAO attributeDAO;
    private ObservableList<AttributeDefinition> attributes;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Check admin access
        if (!SessionManager.getInstance().isAdmin()) {
            AlertManager.showError("Access Denied", "Only administrators can manage attributes");
            handleBack();
            return;
        }

        attributeDAO = new AttributeDAO();
        attributes = FXCollections.observableArrayList();

        setupTable();
        loadData();
    }

    private void setupTable() {
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        data_type_column.setCellValueFactory(new PropertyValueFactory<>("dataType"));
        description_column.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Actions column with Edit and Delete hyperlinks
        actions_column.setCellFactory(new Callback<TableColumn<AttributeDefinition, Void>, TableCell<AttributeDefinition, Void>>() {
            @Override
            public TableCell<AttributeDefinition, Void> call(TableColumn<AttributeDefinition, Void> param) {
                return new TableCell<AttributeDefinition, Void>() {
                    private final Hyperlink editLink = new Hyperlink("Edit");
                    private final Hyperlink deleteLink = new Hyperlink("Delete");

                    {
                        editLink.setOnAction(e -> {
                            AttributeDefinition attribute = getTableView().getItems().get(getIndex());
                            handleEdit(attribute);
                        });

                        deleteLink.setOnAction(e -> {
                            AttributeDefinition attribute = getTableView().getItems().get(getIndex());
                            handleDelete(attribute);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new javafx.scene.layout.HBox(10, editLink, deleteLink));
                        }
                    }
                };
            }
        });

        attributes_table_view.setItems(attributes);
    }
    private void loadData() {
        try {
            attributes.clear();
            attributes.addAll(attributeDAO.findAll());
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load attributes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleNew() {
        openAttributeForm(null);
    }

    private void handleEdit(AttributeDefinition attribute) {
        openAttributeForm(attribute);
    }

    private void openAttributeForm(AttributeDefinition attribute) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/views/internal_views/new-attribute-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            NewAttributeController controller = loader.getController();
            controller.setAttribute(attribute);

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(attribute == null ? "New Attribute" : "Edit Attribute");
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            if (stage != null) {
                dialogStage.initOwner(stage);
            }
            dialogStage.setResizable(false);
            controller.setStage(dialogStage);
            dialogStage.showAndWait();

            // Reload data after form closes
            loadData();
        } catch (Exception e) {
            AlertManager.showError("Error", "Failed to open attribute form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(AttributeDefinition attribute) {
        if (AlertManager.confirmDelete("attribute")) {
            try {
                attributeDAO.delete(attribute.getId());
                loadData();
                AlertManager.showInfo("Success", "Attribute deleted successfully");
            } catch (SQLException e) {
                AlertManager.showError("Database Error", "Failed to delete attribute: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleBack() {
        changeScene("views/internal_views/add-device-view.fxml");
        if (stage != null) {
            stage.setTitle("Add Device");
        }
    }
    public void refreshTable(){
        loadData();
    }



    public void closeAddDeviceView(){
        changeScene("views/internal_views/add-device-view.fxml");
    }

    public void openNewAttributeCreation(){
        openNewWindow("views/internal_views/new-attribute-view.fxml","New Attribute");
    }

}

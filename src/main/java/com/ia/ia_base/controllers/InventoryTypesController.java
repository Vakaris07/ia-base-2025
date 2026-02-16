package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.AttributeDAO;
import com.ia.ia_base.database.DAO.InventoryTypeDAO;
import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.models.InventoryType;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.SQLException;

public class InventoryTypesController extends BaseController {

    @FXML
    private TableView<InventoryType> inventory_type_table_view;
    @FXML
    private TableColumn<InventoryType, Integer> id_column;
    @FXML
    private TableColumn<InventoryType, String> name_column;
    @FXML
    private TableColumn<InventoryType, String> description_column;
    @FXML
    private TableColumn<InventoryType, Void> actions_column;

    private InventoryTypeDAO inventoryTypeDAO;
    private ObservableList<InventoryType> inventoryTypes;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Check admin access
        if (!SessionManager.getInstance().isAdmin()) {
            AlertManager.showError("Access Denied", "Only administrators can manage inventory types");
            handleBack();
            return;
        }

        inventoryTypeDAO = new InventoryTypeDAO();
        inventoryTypes = FXCollections.observableArrayList();

        setupTable();
        loadData();
    }

    private void setupTable() {
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        description_column.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Actions column with Edit and Delete hyperlinks
        actions_column.setCellFactory(new Callback<TableColumn<InventoryType, Void>, TableCell<InventoryType, Void>>() {
            @Override
            public TableCell<InventoryType, Void> call(TableColumn<InventoryType, Void> param) {
                return new TableCell<InventoryType, Void>() {
                    private final Hyperlink editLink = new Hyperlink("Edit");
                    private final Hyperlink deleteLink = new Hyperlink("Delete");

                    {
                        editLink.setOnAction(e -> {
                            InventoryType inventoryType = getTableView().getItems().get(getIndex());
                            handleEdit(inventoryType);
                        });

                        deleteLink.setOnAction(e -> {
                            InventoryType inventoryType = getTableView().getItems().get(getIndex());
                            handleDelete(inventoryType);
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

        inventory_type_table_view.setItems(inventoryTypes);
    }

    private void loadData() {
        try {
            inventoryTypes.clear();
            inventoryTypes.addAll(inventoryTypeDAO.findAll());
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load inventory types: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleNew() {
        openInventoryTypeCreation(null);
    }

    private void handleEdit(InventoryType inventoryType) {
        openInventoryTypeCreation(inventoryType);
    }
    private void openInventoryTypeCreation(InventoryType inventoryType) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/views/internal_views/new-inventory-type-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            NewInventoryTypeController controller = loader.getController();
            controller.setInventoryType(inventoryType);

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(inventoryType == null ? "New Inventory Type" : "Edit Inventory Type");
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

    private void handleDelete(InventoryType inventoryType) {
        if (AlertManager.confirmDelete("attribute")) {
            try {
                inventoryTypeDAO.delete(inventoryType.getId());
                loadData();
                AlertManager.showInfo("Success", "Inventory type deleted successfully");
            } catch (SQLException e) {
                AlertManager.showError("Database Error", "Failed to delete inventory type: " + e.getMessage());
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
    @FXML
    public void refreshTable() {
        loadData();
    }
    @FXML
    public void closeAddInventoryTypeView() {
        changeScene("views/internal_views/add-device-view.fxml");
    }
    @FXML
    public void openInventoryTypesView() {
        changeScene("views/internal_views/new-inventory-type-view.fxml");
    }





}

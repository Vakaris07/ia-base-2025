package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.AttributeDAO;
import com.ia.ia_base.database.DAO.InventoryAttributeDAO;
import com.ia.ia_base.database.DAO.InventoryDAO;
import com.ia.ia_base.models.Inventory;
import com.ia.ia_base.models.InventoryAttribute;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import com.ia.ia_base.util.WindowManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryViewController extends BaseController{
    @FXML
    private TableView<Inventory> inventory_table_view;
    @FXML
    private TableColumn<Inventory, Integer> sku_column;
    @FXML
    private TableColumn<Inventory, String> title_column;
    @FXML
    private TableColumn<Inventory, String> description_column;
    @FXML
    private TableColumn<Inventory, String> inventory_type_column;
    @FXML
    private TableColumn<Inventory, Integer> location_column;
    @FXML
    private TableColumn<Inventory, Void> actions_column;

    private InventoryDAO inventoryDAO;
    private InventoryAttributeDAO inventoryAttributeDAO;
    private AttributeDAO attributeDAO;
    private ObservableList<Inventory> allInventory;
    private Map<Integer, List<InventoryAttribute>> inventoryAttributesMap; // Map product SKU -> list of attributes


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        try {
            inventoryDAO = new InventoryDAO();
            inventoryAttributeDAO = new InventoryAttributeDAO();
            attributeDAO = new AttributeDAO();
            inventoryAttributesMap = new HashMap<>();
            allInventory = FXCollections.observableArrayList();
            setupTable();
            loadData();
        } catch (Exception e) {
            System.err.println("Error initializing InventoryViewController: " + e.getMessage());
            e.printStackTrace();
            // Don't show error dialog - might prevent window from opening
            // Just log the error and continue
        }
    }

    public void setupTable() {
        sku_column.setCellValueFactory(new PropertyValueFactory<>("SKU"));
        title_column.setCellValueFactory(new PropertyValueFactory<>("title"));
        description_column.setCellValueFactory(new PropertyValueFactory<>("description"));
        inventory_type_column.setCellValueFactory(cellData ->{
                Inventory i = cellData.getValue();
                String type = i.getInventoryType() != null ? i.getInventoryType().getName() : "---";
                return new javafx.beans.property.ReadOnlyObjectWrapper<>(type);
                }
        );
        location_column.setCellValueFactory(cellData -> {
            Inventory i = cellData.getValue();
            int categoryId = i.getLocation() != null ? i.getLocation().getId() : 0;
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(categoryId);
        });

        actions_column.setCellFactory(new Callback<TableColumn<Inventory, Void>, TableCell<Inventory, Void>>() {
            @Override
            public TableCell<Inventory, Void> call(TableColumn<Inventory, Void> param) {
                return new TableCell<Inventory, Void>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");

                    {
                        editBtn.setOnAction(e -> {
                            Inventory inventory = getTableView().getItems().get(getIndex());
                            handleEdit(inventory);
                        });

                        deleteBtn.setOnAction(e -> {
                            Inventory inventory = getTableView().getItems().get(getIndex());
                            handleDelete(inventory);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Inventory inventory = getTableView().getItems().get(getIndex());
                            boolean canEdit = canEditInventory(inventory);
                            boolean canDelete = canDeleteInventory(inventory);

                            editBtn.setDisable(!canEdit);
                            deleteBtn.setDisable(!canDelete);

                            setGraphic(new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
                        }
                    }
                };
            }
        });

        // Set filtered list to table (already created in initialize)
        inventory_table_view.setItems(allInventory);
    }
    private boolean canEditInventory(Inventory inventory) {
        var session = SessionManager.getInstance();
        if (session.isAdmin()) {
            return true; // Admin can edit all
        }
        // Registered user can only edit their own products
        return inventory.getCreatedByUserId() == session.getCurrentUser().getId();
    }

    private boolean canDeleteInventory(Inventory inventory) {
        var session = SessionManager.getInstance();
        if (session.isAdmin()) {
            return true; // Admin can delete all
        }
        // Registered user can only delete their own products
        return inventory.getCreatedByUserId() == session.getCurrentUser().getId();
    }

    public void loadData() {
        try {
            if (allInventory == null) {
                allInventory = FXCollections.observableArrayList();
            }
            if (inventoryAttributesMap == null) {
                inventoryAttributesMap = new HashMap<>();
            }

            allInventory.clear();
            inventoryAttributesMap.clear();
            // Load attributes for all products
            var allInventoryFromDB = inventoryDAO.findAll();
            for (Inventory inventory : allInventoryFromDB) {
                try {
                    List<InventoryAttribute> attributes = inventoryAttributeDAO.findByInventorySku(inventory.getSKU());
                    inventoryAttributesMap.put(inventory.getSKU(), attributes);
                    inventory.setAttributes(attributes);
                } catch (SQLException e) {
                    // If attributes can't be loaded, just use empty list
                    inventoryAttributesMap.put(inventory.getSKU(), new java.util.ArrayList<>());
                    inventory.setAttributes(new java.util.ArrayList<>());
                }
            }


            allInventory.addAll(allInventoryFromDB);


        } catch (SQLException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
            e.printStackTrace();
            // Don't show error dialog - might be that tables don't exist yet
            // Just show empty table
        } catch (Exception e) {
            System.err.println("Unexpected error loading inventory: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    private void handleNew() {
        openInventoryCreation(null);
    }

    private void handleEdit(Inventory inventory) {
        if (!canEditInventory(inventory)) {
            AlertManager.showError("Access Denied", "You can only edit your own products");
            return;
        }
        openInventoryCreation(inventory);
    }

    private void openInventoryCreation(Inventory inventory) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/views/internal_views/add-device-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            AddDeviceViewController controller = loader.getController();
            controller.setInventory(inventory);

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(inventory == null ? "New Inventory Addition" : "Edit Inventory ");
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
            AlertManager.showError("Error", "Failed to open inventory creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(Inventory inventory) {
        if (!canDeleteInventory(inventory)) {
            AlertManager.showError("Access Denied", "You can only delete your own inventory additions");
            return;
        }

        if (AlertManager.confirmDelete("inventory")) {
            try {
                inventoryDAO.delete(inventory.getSKU());
                loadData();
                AlertManager.showInfo("Success", "Inventory addition successfully deleted");
            } catch (SQLException e) {
                AlertManager.showError("Database Error", "Failed to delete inventory addition: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }



    public void openFilters(){
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/views/internal_views/filter-view.fxml")
            );
            javafx.scene.Parent root = loader.load();

            FilterViewController controller = loader.getController();
            controller.setInventory(this, allInventory); // inject REAL controller + list

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Filtering Device Inventory");
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            if (stage != null) dialogStage.initOwner(stage);

            controller.setStage(dialogStage);
            dialogStage.show();

        } catch (Exception e) {
            AlertManager.showError("Error", "Failed to open filters: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void openAddDevice(){
        openNewWindow("views/internal_views/add-device-view.fxml","Add Device to Inventory");
    }

    public ObservableList<Inventory> getAllInventory(){
        return allInventory;
    }
    public TableView<Inventory> getTable(){
        //loadData();
        return inventory_table_view;
    }

    @FXML
    private void clear(){
        setupTable();
        loadData();
    }
}

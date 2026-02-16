package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.*;
import com.ia.ia_base.models.*;

import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDeviceViewController extends BaseController {
    @FXML
    private VBox attributes_vbox;
    @FXML
    private Button cancel_button;
    @FXML
    private Button done_button;
    @FXML
    private Button attributes_button;
    @FXML
    private TextField title_text_field;
    @FXML
    private TextField description_text_field;
    @FXML
    private ComboBox<InventoryType> inventory_type_combo;
    @FXML
    private ComboBox<Location> location_combo;
    @FXML
    private ScrollPane attributes_scroll_pane;

    private InventoryDAO inventoryDAO;
    private InventoryTypeDAO inventoryTypeDAO;
    private LocationDAO locationDAO;
    private InventoryTypeAttributeDAO inventoryTypeAttributeDAO;
    private InventoryAttributeDAO inventoryAttributeDAO;
    private Inventory inventory;
    private Map<AttributeDefinition, TextField> attributeFields; // Map to store attribute value fields
    private List<AttributeDefinition> availableAttributes;


    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        // Load data first, then set inventory
        loadLocations();
        loadInventoryTypes();

        if (inventory != null) {
            title_text_field.setText(inventory.getTitle());
            description_text_field.setText(inventory.getDescription() != null ? inventory.getDescription() : "");

            // Select inventory type
            if (inventory.getInventoryType() != null && inventory.getInventoryType().getId() > 0) {
                inventory_type_combo.getItems().stream()
                        .filter(it -> it.getId() == inventory.getInventoryType().getId())
                        .findFirst()
                        .ifPresent(it -> {
                            inventory_type_combo.getSelectionModel().select(it);
                            loadAttributesForInventoryType(it);
                        });
            }

            // Select location in ComboBox
            if (inventory.getLocation() != null && inventory.getLocation().getId() > 0) {
                location_combo.getItems().stream()
                        .filter(l -> l.getId() == inventory.getLocation().getId())
                        .findFirst()
                        .ifPresent(l -> location_combo.getSelectionModel().select(l));
            }


            // Load existing product attributes
            loadInventoryAttributes();
        }
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        inventoryDAO = new InventoryDAO();
        inventoryTypeDAO = new InventoryTypeDAO();
        locationDAO = new LocationDAO();
        inventoryTypeAttributeDAO = new InventoryTypeAttributeDAO();
        inventoryAttributeDAO = new InventoryAttributeDAO();
        attributeFields = new HashMap<>();
        availableAttributes = new ArrayList<>();
        loadLocations();
        loadInventoryTypes();

        // Setup inventory type change listener
        inventory_type_combo.setOnAction(e -> {
            InventoryType selected = inventory_type_combo.getSelectionModel().getSelectedItem();
            if (selected != null) {
                loadAttributesForInventoryType(selected);
            }
        });
    }

    private void loadInventoryTypes() {
        try {
            ObservableList<InventoryType> inventoryTypes = FXCollections.observableArrayList(inventoryTypeDAO.findAll());
            inventory_type_combo.setItems(inventoryTypes);
            inventory_type_combo.setCellFactory(param -> new javafx.scene.control.ListCell<InventoryType>() {
                @Override
                protected void updateItem(InventoryType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(item.getName());
                    }
                }
            });
            inventory_type_combo.setButtonCell(new javafx.scene.control.ListCell<InventoryType>() {
                @Override
                protected void updateItem(InventoryType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Product Type");
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load inventory types: " + e.getMessage());
        }
    }

    private void loadLocations() {
        try {
            ObservableList<Location> locations = FXCollections.observableArrayList(locationDAO.findAll());
            location_combo.setItems(locations);
            location_combo.setCellFactory(param -> new javafx.scene.control.ListCell<Location>() {
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(item.getSchoolSector() + ", " + item.getRoomNumber());
                    }
                }
            });
            location_combo.setButtonCell(new javafx.scene.control.ListCell<Location>() {
                @Override
                protected void updateItem(Location item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(item.getSchoolSector() + ", " + item.getRoomNumber());
                    }
                }
            });
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load locations: " + e.getMessage());
        }
    }

    private void loadAttributesForInventoryType(InventoryType inventoryType) {
        try {
            availableAttributes.clear();
            if (inventoryType != null && inventoryType.getId() > 0) {
                availableAttributes.addAll(inventoryTypeAttributeDAO.findAttributesByInventoryTypeId(inventoryType.getId()));
            }
            buildAttributeFields();

            // Reload existing product attributes if editing
            if (inventory != null) {
                loadInventoryAttributes();
            }
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load attributes for product type: " + e.getMessage());
        }
    }

    private void buildAttributeFields() {
        attributes_vbox.getChildren().clear();
        attributeFields.clear();

        if (availableAttributes.isEmpty()) {
            Label noAttributesLabel = new Label("No attributes available. Please create attributes first.");
            attributes_vbox.getChildren().add(noAttributesLabel);
            return;
        }

        for (AttributeDefinition attr : availableAttributes) {
            HBox attributeRow = new HBox(10);
            attributeRow.setPadding(new javafx.geometry.Insets(5));

            Label label = new Label(attr.getName() + ":");
            label.setPrefWidth(150);

            TextField valueField = new TextField();
            valueField.setPromptText("Enter " + attr.getName().toLowerCase());
            valueField.setPrefWidth(200);

            attributeFields.put(attr, valueField);

            Label typeLabel = new Label("(" + attr.getDataType() + ")");
            typeLabel.setStyle("-fx-text-fill: gray;");

            attributeRow.getChildren().addAll(label, valueField, typeLabel);
            attributes_vbox.getChildren().add(attributeRow);
        }
    }

    private void loadInventoryAttributes() {
        if (inventory == null || inventory.getSKU() == 0) {
            return;
        }

        try {
            List<InventoryAttribute> inventoryAttributes = inventoryAttributeDAO.findByInventorySku(inventory.getSKU());
            for (InventoryAttribute ia : inventoryAttributes) {
                TextField field = attributeFields.get(ia.getAttribute());
                if (field != null) {
                    field.setText(ia.getAttributeValue());
                }
            }
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load inventory attributes: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        String title = title_text_field.getText().trim();
        String description = description_text_field.getText().trim();


        if (title.isEmpty()) {
            AlertManager.showError("Validation Error", "Title cannot be empty");
            return;
        }

        try {
            Location selectedLocation = location_combo.getSelectionModel().getSelectedItem();
            InventoryType selectedInventoryType = inventory_type_combo.getSelectionModel().getSelectedItem();

            if (inventory == null) {
                // Create new
                if (selectedLocation == null) {
                    AlertManager.showError("Validation Error", "Please select a location");
                    return;
                }

                if (selectedInventoryType == null) {
                    AlertManager.showError("Validation Error", "Please select an inventory type");
                    return;
                }

                // Create product
                Inventory newInventory = new ConcreteInventory(0, title, description, selectedLocation);
                newInventory.setInventoryType(selectedInventoryType);
                newInventory.setCreatedByUserId(SessionManager.getInstance().getCurrentUser().getId());
                inventoryDAO.create(newInventory);

                // Get the created product's SKU (assuming it's auto-generated)
                // For now, we'll need to reload to get the SKU, or modify DAO to return it
                // This is a simplified approach - in production, you'd want to return the generated key
                int newSku = getLastInsertedSku();
                if (newSku > 0) {
                    saveInventoryAttributes(newSku);
                }

                AlertManager.showInfo("Success", "Inventory addition created successfully");
            } else {
                // Update existing - check permissions
                if (!SessionManager.getInstance().isAdmin() &&
                        inventory.getCreatedByUserId() != SessionManager.getInstance().getCurrentUser().getId()) {
                    AlertManager.showError("Access Denied", "You can only edit your own inventory");
                    return;
                }

                if (selectedLocation == null) {
                    AlertManager.showError("Validation Error", "Please select a location");
                    return;
                }

                if (selectedInventoryType == null) {
                    AlertManager.showError("Validation Error", "Please select an inventory type");
                    return;
                }

                inventory.setTitle(title);
                inventory.setDescription(description);
                inventory.setLocation(selectedLocation);
                inventory.setInventoryType(selectedInventoryType);
                inventoryDAO.update(inventory);

                // Update product attributes
                saveInventoryAttributes(inventory.getSKU());

                AlertManager.showInfo("Success", "Inventory updated successfully");
            }

            closeWindow();
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to save product: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getLastInsertedSku() {
        try {
            // Get the last inserted product SKU
            // This is a simplified approach - in production, use getGeneratedKeys()
            List<Inventory> allProducts = inventoryDAO.findAll();
            if (allProducts.isEmpty()) {
                return 1;
            }
            return allProducts.stream()
                    .mapToInt(Inventory::getSKU)
                    .max()
                    .orElse(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void saveInventoryAttributes(int inventorySku) {
        try {
            // Delete existing attributes for this product
            inventoryAttributeDAO.deleteByInventorySku(inventorySku);

            // Save new attributes
            for (Map.Entry<AttributeDefinition, TextField> entry : attributeFields.entrySet()) {
                AttributeDefinition attr = entry.getKey();
                TextField field = entry.getValue();
                String value = field.getText().trim();

                // Only save if value is not empty
                if (!value.isEmpty()) {
                    InventoryAttribute ia = new InventoryAttribute();
                    ia.setInventorySku(inventorySku);
                    ia.setAttribute(attr);
                    ia.setAttributeValue(value);
                    inventoryAttributeDAO.create(ia);
                }
            }
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to save inventory attributes: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void openAttributes() {
        changeScene("views/internal_views/device-attributes.fxml");
    }

    public void openLocations() {
        changeScene("views/internal_views/locations.fxml");
    }

    public void openInventoryTypes() {
        changeScene("views/internal_views/inventory-types.fxml");
    }

    public void closeAddDeviceView() {
        closeWindow();
    }



}

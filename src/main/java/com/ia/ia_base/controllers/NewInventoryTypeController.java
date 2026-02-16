package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.AttributeDAO;
import com.ia.ia_base.database.DAO.InventoryTypeAttributeDAO;
import com.ia.ia_base.database.DAO.InventoryTypeDAO;
import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.models.InventoryType;
import com.ia.ia_base.util.AlertManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewInventoryTypeController extends BaseController{

    @FXML
    private VBox attributes_container;
    @FXML
    private TextField inventory_type_name_field;
    @FXML
    private TextField inventory_type_description_field;

    private InventoryTypeDAO inventoryTypeDAO;
    private AttributeDAO attributeDAO;
    private InventoryTypeAttributeDAO inventoryTypeAttributeDAO;
    private InventoryType inventoryType; // null for new, existing for edit
    private List<AttributeDefinition> allAttributes;
    private Set<Integer> selectedAttributeIds;

    public void setInventoryType(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
        loadAttributes();

        if (inventoryType != null) {
            inventory_type_name_field.setText(inventoryType.getName());
            inventory_type_description_field.setText(inventoryType.getDescription() != null ? inventoryType.getDescription() : "");
            loadSelectedAttributes();
        } else {
            selectedAttributeIds = new HashSet<>();
        }
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        inventoryTypeDAO = new InventoryTypeDAO();
        attributeDAO = new AttributeDAO();
        inventoryTypeAttributeDAO = new InventoryTypeAttributeDAO();
        allAttributes = new ArrayList<>();
        selectedAttributeIds = new HashSet<>();
        loadAttributes();
    }

    private void loadAttributes() {
        try {
            allAttributes.clear();
            allAttributes.addAll(attributeDAO.findAll());
            buildAttributeCheckboxes();
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load attributes: " + e.getMessage());
        }
    }

    private void loadSelectedAttributes() {
        if (inventoryType == null || inventoryType.getId() == 0) {
            selectedAttributeIds = new HashSet<>();
            return;
        }

        try {
            List<AttributeDefinition> selected = inventoryTypeAttributeDAO.findAttributesByInventoryTypeId(inventoryType.getId());
            selectedAttributeIds = new HashSet<>();
            for (AttributeDefinition attr : selected) {
                selectedAttributeIds.add(attr.getId());
            }
            buildAttributeCheckboxes();
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load selected attributes: " + e.getMessage());
        }
    }

    private void buildAttributeCheckboxes() {
        attributes_container.getChildren().clear();

        if (allAttributes.isEmpty()) {
            Label noAttributesLabel = new Label("No attributes available. Please create attributes first.");
            attributes_container.getChildren().add(noAttributesLabel);
            return;
        }

        for (AttributeDefinition attr : allAttributes) {
            HBox attributeRow = new HBox(10);
            attributeRow.setPadding(new javafx.geometry.Insets(5));

            CheckBox checkBox = new CheckBox(attr.getName());
            checkBox.setSelected(selectedAttributeIds.contains(attr.getId()));
            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()) {
                    selectedAttributeIds.add(attr.getId());
                } else {
                    selectedAttributeIds.remove(attr.getId());
                }
            });

            Label typeLabel = new Label("(" + attr.getDataType() + ")");
            typeLabel.setStyle("-fx-text-fill: gray;");

            attributeRow.getChildren().addAll(checkBox, typeLabel);
            attributes_container.getChildren().add(attributeRow);
        }
    }

    @FXML
    private void confirm() {
        String name = inventory_type_name_field.getText().trim();
        String description = inventory_type_description_field.getText().trim();

        if (name.isEmpty()) {
            AlertManager.showError("Validation Error", "Name cannot be empty");
            return;
        }

        try {
            if (inventoryType == null) {
                // Create new
                InventoryType newInventoryType = new InventoryType();
                newInventoryType.setName(name);
                newInventoryType.setDescription(description);
                inventoryTypeDAO.create(newInventoryType);

                // Get the created product type ID (simplified - in production use getGeneratedKeys)
                List<InventoryType> all = inventoryTypeDAO.findAll();
                int newId = all.stream().filter(it -> it.getName().equals(name)).findFirst()
                        .map(InventoryType::getId).orElse(0);

                if (newId > 0) {
                    saveInventoryTypeAttributes(newId);
                }

                AlertManager.showInfo("Success", "Product type created successfully");
            } else {
                // Update existing
                inventoryType.setName(name);
                inventoryType.setDescription(description);
                inventoryTypeDAO.update(inventoryType);

                // Update attributes
                saveInventoryTypeAttributes(inventoryType.getId());

                AlertManager.showInfo("Success", "Inventory type updated successfully");
            }

            changeScene("views/internal_views/inventory-types.fxml");
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to save inventory type: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveInventoryTypeAttributes(int inventoryTypeId) throws SQLException {
        // Remove all existing attributes
        inventoryTypeAttributeDAO.removeAllAttributesFromProductType(inventoryTypeId);

        // Add selected attributes
        for (Integer attributeId : selectedAttributeIds) {
            inventoryTypeAttributeDAO.addAttributeToInventoryType(inventoryTypeId, attributeId);
        }
    }

    @FXML
    private void cancel() {
        changeScene("views/internal_views/inventory-types.fxml");;
    }
}

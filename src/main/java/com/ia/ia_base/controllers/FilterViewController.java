package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.AttributeDAO;
import com.ia.ia_base.database.DAO.InventoryAttributeDAO;
import com.ia.ia_base.database.DAO.InventoryDAO;
import com.ia.ia_base.database.DAO.InventoryTypeDAO;
import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.models.Inventory;
import com.ia.ia_base.models.InventoryAttribute;
import com.ia.ia_base.models.InventoryType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterViewController extends BaseController{

    @FXML
    private Button clear_filters_button;
    @FXML
    private ComboBox<InventoryType> inventory_type_combo;
    @FXML
    private ComboBox<AttributeDefinition> attribute_combo;
    @FXML
    private TextField title_text_field;
    @FXML
    private TextField attribute_value_field;

    private FilteredList<Inventory> filteredInventory;
    private InventoryTypeDAO inventoryTypeDAO;
    private AttributeDAO attributeDAO;
    private InventoryDAO inventoryDAO;
    private InventoryAttributeDAO inventoryAttributeDAO;
    private ObservableList<Inventory> allInventory;
    private InventoryViewController inventoryController;
    private Map<Integer, List<InventoryAttribute>> inventoryAttributesMap;



    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        try {
            inventoryDAO = new InventoryDAO();
            inventoryTypeDAO = new InventoryTypeDAO();
            attributeDAO = new AttributeDAO();
            inventoryAttributeDAO = new InventoryAttributeDAO();
            inventoryController = new InventoryViewController();
            allInventory = inventoryController.getAllInventory();
            inventoryAttributesMap = new HashMap<>();

            // Create filtered list first
            //filteredInventory = new FilteredList<>(allInventory, i -> true);

            //inventoryController.setupTable();
            setupFilters();
            //inventoryController.loadData();
        } catch (Exception e) {
            System.err.println("Error initializing InventoryViewController: " + e.getMessage());
            e.printStackTrace();
            // Don't show error dialog - might prevent window from opening
            // Just log the error and continue
        }
    }

    public void closeFilterView(){
        closeWindow();
    }

    private void setupFilters() {
        if (inventory_type_combo == null || attribute_combo == null ||
                title_text_field == null || attribute_value_field == null || clear_filters_button == null) {
            return; // FXML elements not loaded yet
        }

        // Setup product type filter
        try {
            ObservableList<InventoryType> inventoryTypes = FXCollections.observableArrayList(inventoryTypeDAO.findAll());
            inventoryTypes.add(0, null); // Add "All" option
            inventory_type_combo.setItems(inventoryTypes);
            inventory_type_combo.setCellFactory(param -> new javafx.scene.control.ListCell<InventoryType>() {
                @Override
                protected void updateItem(InventoryType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("All Types");
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
                        setText("All Types");
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (SQLException e) {
            System.err.println("Warning: Failed to load inventory types for filter: " + e.getMessage());
            // Don't show error dialog here - might be that table doesn't exist yet
        } catch (Exception e) {
            System.err.println("Error setting up inventory type filter: " + e.getMessage());
            e.printStackTrace();
        }

        // Setup attribute filter
        try {
            ObservableList<AttributeDefinition> attributes = FXCollections.observableArrayList(attributeDAO.findAll());
            attributes.add(0, null); // Add "All" option
            attribute_combo.setItems(attributes);
            attribute_combo.setCellFactory(param -> new javafx.scene.control.ListCell<AttributeDefinition>() {
                @Override
                protected void updateItem(AttributeDefinition item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("All Attributes");
                    } else {
                        setText(item.getName());
                    }
                }
            });
            attribute_combo.setButtonCell(new javafx.scene.control.ListCell<AttributeDefinition>() {
                @Override
                protected void updateItem(AttributeDefinition item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("All Attributes");
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (SQLException e) {
            System.err.println("Warning: Failed to load attributes for filter: " + e.getMessage());
            // Don't show error dialog here - might be that table doesn't exist yet
        } catch (Exception e) {
            System.err.println("Error setting up attribute filter: " + e.getMessage());
            e.printStackTrace();
        }

        // Setup filter listeners (only if elements exist)
        if (title_text_field != null) {
            title_text_field.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }
        if (inventory_type_combo != null) {
            inventory_type_combo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }
        if (attribute_combo != null) {
            attribute_combo.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }
        if (attribute_value_field != null) {
            attribute_value_field.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        }

        // Clear filters button
        if (clear_filters_button != null) {
            clear_filters_button.setOnAction(e -> clearFilters());
        }
    }

    private void applyFilters() {
        if (filteredInventory == null || title_text_field == null) {
            return; // Not initialized yet
        }

        String titleFilter = title_text_field.getText() != null ? title_text_field.getText().toLowerCase().trim() : "";
        InventoryType typeFilter = inventory_type_combo != null ? inventory_type_combo.getSelectionModel().getSelectedItem() : null;
        AttributeDefinition attributeFilter = attribute_combo != null ? attribute_combo.getSelectionModel().getSelectedItem() : null;
        String attributeValueFilter = attribute_value_field != null && attribute_value_field.getText() != null ?
                attribute_value_field.getText().toLowerCase().trim() : "";

        filteredInventory.setPredicate(inventory -> {
            // Filter by title
            if (!titleFilter.isEmpty()) {
                String title = inventory.getTitle() != null ? inventory.getTitle().toLowerCase() : "";
                if (!title.contains(titleFilter)) {
                    return false;
                }
            }

            // Filter by inventory type
            if (typeFilter != null) {
                if (inventory.getInventoryType() == null ||
                        inventory.getInventoryType().getId() != typeFilter.getId()) {
                    return false;
                }
            }

            // Filter by attribute
            if (attributeFilter != null) {
                List<InventoryAttribute> inventoryAttrs = inventoryAttributesMap.get(inventory.getSKU());
                if (inventoryAttrs == null || inventoryAttrs.isEmpty()) {
                    return false; // inventory has no attributes
                }

                // Check if inventory has the selected attribute
                boolean hasAttribute = inventoryAttrs.stream()
                        .anyMatch(pa -> pa.getAttribute() != null &&
                                pa.getAttribute().getId() == attributeFilter.getId());

                if (!hasAttribute) {
                    return false;
                }

                // Filter by attribute value if specified
                if (!attributeValueFilter.isEmpty()) {
                    boolean hasMatchingValue = inventoryAttrs.stream()
                            .anyMatch(ia -> ia.getAttribute() != null &&
                                    ia.getAttribute().getId() == attributeFilter.getId() &&
                                    ia.getAttributeValue() != null &&
                                    ia.getAttributeValue().toLowerCase().contains(attributeValueFilter));

                    if (!hasMatchingValue) {
                        return false;
                    }
                }
            }

            return true;
        });
    }
    @FXML
    private void clearFilters() {
        if (title_text_field != null) {
            title_text_field.clear();
        }
        if (inventory_type_combo != null) {
            inventory_type_combo.getSelectionModel().select(null);
        }
        if (attribute_combo != null) {
            attribute_combo.getSelectionModel().select(null);
        }
        if (attribute_value_field != null) {
            attribute_value_field.clear();
        }
        applyFilters();
        //closeFilterView();
    }
    @FXML
    public void apply(){
        if (inventoryController != null && filteredInventory != null) {
            inventoryController.getTable().setItems(filteredInventory);
        }
        closeWindow();
    }

    public void setInventory(InventoryViewController inventoryController,
                                    ObservableList<Inventory> allInventory) {
        this.inventoryController = inventoryController;
        this.allInventory = (allInventory != null) ? allInventory : FXCollections.observableArrayList();

        this.filteredInventory = new FilteredList<>(this.allInventory, i -> true);
        applyFilters();
    }
}

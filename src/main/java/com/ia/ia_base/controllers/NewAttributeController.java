package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.AttributeDAO;
import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class NewAttributeController extends BaseController{

    @FXML
    private TextField attribute_name_text_field;
    @FXML
    private ComboBox<String> attribute_type_combo_box;// number arba text
    @FXML
    private Button cancel_button;
    @FXML
    private Button done_button;
    @FXML
    private TextField attribute_description_text_field;

    private AttributeDAO attributeDAO;
    private AttributeDefinition attribute; // null for new, existing for edit

    public void setAttribute(AttributeDefinition attribute) {
        this.attribute = attribute;

        if (attribute != null) {
            attribute_name_text_field.setText(attribute.getName());
            attribute_description_text_field.setText(attribute.getDescription() != null ? attribute.getDescription() : "");

            // Select data type in ComboBox
            if (attribute.getDataType() != null) {
                attribute_type_combo_box.getSelectionModel().select(attribute.getDataType());
            }
        }
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        attributeDAO = new AttributeDAO();

        // Setup data type options
        ObservableList<String> dataTypes = FXCollections.observableArrayList(
                "STRING", "NUMBER", "BOOLEAN", "DATE"
        );
        attribute_type_combo_box.setItems(dataTypes);
        attribute_type_combo_box.getSelectionModel().selectFirst(); // Default to STRING
    }
    //attribute_type_combo_box.setItems(choices);



    public void closeAddDeviceView(){
        closeWindow();
    }
    @FXML
    private void confirm(){
        String name = attribute_name_text_field.getText().trim();
        String dataType = attribute_type_combo_box.getSelectionModel().getSelectedItem();
        String description = attribute_description_text_field.getText().trim();

        if (name.isEmpty()) {
            AlertManager.showError("Validation Error", "Name cannot be empty");
            return;
        }

        if (dataType == null) {
            AlertManager.showError("Validation Error", "Please select a data type");
            return;
        }

        try {
            if (attribute == null) {
                // Create new
                AttributeDefinition newAttribute = new AttributeDefinition();
                newAttribute.setName(name);
                newAttribute.setDataType(dataType);
                newAttribute.setDescription(description);
                attributeDAO.create(newAttribute);
                AlertManager.showInfo("Success", "Attribute created successfully");
            } else {
                // Update existing
                attribute.setName(name);
                attribute.setDataType(dataType);
                attribute.setDescription(description);
                attributeDAO.update(attribute);
                AlertManager.showInfo("Success", "Attribute updated successfully");
            }

            closeWindow();
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to save attribute: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

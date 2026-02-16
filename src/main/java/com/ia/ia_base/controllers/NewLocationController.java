package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.LocationDAO;
import com.ia.ia_base.models.Location;
import com.ia.ia_base.util.AlertManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class NewLocationController extends BaseController{
    @FXML
    private TextField school_sector_field;
    @FXML
    private TextField room_number_field;

    private LocationDAO locationDAO;
    private Location location; // null for new, existing for edit

    public void setLocation(Location location) {
        this.location = location;

        if (location != null) {
            school_sector_field.setText(location.getSchoolSector());
            room_number_field.setText(location.getRoomNumber() != null ? location.getRoomNumber() : "");
        }
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        locationDAO = new LocationDAO();


    }
    public void closeAddDeviceView(){
        closeWindow();
    }
    @FXML
    private void confirm(){
        String schoolSector = school_sector_field.getText().trim();
        String roomNumber = room_number_field.getText().trim();

        if (schoolSector.isEmpty()) {
            AlertManager.showError("Validation Error", "School sector cannot be empty");
            return;
        }

        try {
            if (location == null) {
                // Create new
                Location newLocation = new Location();
                newLocation.setSchoolSector(schoolSector);
                newLocation.setRoomNumber(roomNumber);

                locationDAO.create(newLocation);
                AlertManager.showInfo("Success", "Location created successfully");
            } else {
                // Update existing
                location.setSchoolSector(schoolSector);
                location.setRoomNumber(roomNumber);

                locationDAO.update(location);
                AlertManager.showInfo("Success", "Location updated successfully");
            }

            closeWindow();
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to save attribute: " + e.getMessage());
            e.printStackTrace();
        }
    }

}


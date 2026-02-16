package com.ia.ia_base.controllers;

import com.ia.ia_base.database.DAO.LocationDAO;
import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.util.AlertManager;
import com.ia.ia_base.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.ia.ia_base.models.Location;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.SQLException;


public class LocationsController extends BaseController{



    @FXML
    private Button cancel_button;
    @FXML
    private Button done_button;
    @FXML
    private TableView<Location> locations_table_view;
    @FXML
    private TableColumn<Location, Integer> id_column;
    @FXML
    private TableColumn<Location, String> school_sector_column;
    @FXML
    private TableColumn<Location, String> room_number_column;
    @FXML
    private TableColumn<Location, Void> actions_column;
    @FXML
    private Button new_locations_button;
    @FXML
    private Button refresh_button;

    private LocationDAO locationDAO;
    private ObservableList<Location> locations;


    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        // Check admin access
        if (!SessionManager.getInstance().isAdmin()) {
            AlertManager.showError("Access Denied", "Only administrators can manage attributes");
            handleBack();
            return;
        }

        locationDAO = new LocationDAO();
        locations = FXCollections.observableArrayList();

        setupTable();
        loadData();
    }

    private void setupTable() {
        id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        school_sector_column.setCellValueFactory(new PropertyValueFactory<>("schoolSector"));
        room_number_column.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));


        // Actions column with Edit and Delete hyperlinks
        actions_column.setCellFactory(new Callback<TableColumn<Location, Void>, TableCell<Location, Void>>() {
            @Override
            public TableCell<Location, Void> call(TableColumn<Location, Void> param) {
                return new TableCell<Location, Void>() {
                    private final Hyperlink editLink = new Hyperlink("Edit");
                    private final Hyperlink deleteLink = new Hyperlink("Delete");

                    {
                        editLink.setOnAction(e -> {
                            Location location = getTableView().getItems().get(getIndex());
                            handleEdit(location);
                        });

                        deleteLink.setOnAction(e -> {
                            Location location = getTableView().getItems().get(getIndex());
                            handleDelete(location);
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

        locations_table_view.setItems(locations);
    }

    private void loadData() {
        try {
            locations.clear();
            locations.addAll(locationDAO.findAll());
        } catch (SQLException e) {
            AlertManager.showError("Database Error", "Failed to load locations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(Location location) {
        openLocationForm(location);
    }

    private void openLocationForm(Location location) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/ia/ia_base/views/internal_views/new-location-view.fxml")
            );
            javafx.scene.Parent root = loader.load();
            NewLocationController controller = loader.getController();
            controller.setLocation(location);

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(location == null ? "New Location" : "Edit Location");
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
            AlertManager.showError("Error", "Failed to open location creation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDelete(Location location) {
        if (AlertManager.confirmDelete("attribute")) {
            try {
                locationDAO.delete(location.getId());
                loadData();
                AlertManager.showInfo("Success", "Location deleted successfully");
            } catch (SQLException e) {
                AlertManager.showError("Database Error", "Failed to delete location: " + e.getMessage());
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

    public void openNewLocationCreation(){
        openNewWindow("views/internal_views/new-location-view.fxml","New Location");
    }

    public void closeAddLocationsView(){
        changeScene("views/internal_views/add-device-view.fxml");
    }

}

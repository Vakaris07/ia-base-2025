module com.ia.ia_base {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.ia.ia_base to javafx.fxml;
    opens com.ia.ia_base.controllers to javafx.fxml;
    
    exports com.ia.ia_base;
    exports com.ia.ia_base.controllers;
    exports com.ia.ia_base.database;
    exports com.ia.ia_base.util;
    exports com.ia.ia_base.config;
    exports com.ia.ia_base.database.models.example;
}
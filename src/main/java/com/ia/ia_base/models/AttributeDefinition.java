package com.ia.ia_base.models;

import java.util.List;

public class AttributeDefinition {
    private int id;           // e.g. "cpu", "ram", "serialNumber"
    private String name;
    private String dataType;          // e.g. "STRING", "NUMBER" ...
    private String description;

    public AttributeDefinition() { }

    public AttributeDefinition(int id, String name, String dataType, String description) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " (" + dataType + ")";
    }

}

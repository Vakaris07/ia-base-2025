package com.ia.ia_base.models;

import java.util.ArrayList;
import java.util.List;

public class InventoryType {
    private int id;
    private String name;// e.g. "Laptop", "Phone"
    private String description;
    private List<AttributeDefinition> attributes;


    public InventoryType() {
        this.attributes = new ArrayList<>();
    }

    public InventoryType(int id, String name, String description) {
        this();
        this.id = id;
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AttributeDefinition> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeDefinition> attributes) {
        this.attributes = attributes != null ? attributes : new ArrayList<>();
    }

    @Override
    public String toString() {
        return name;
    }
}

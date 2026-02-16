package com.ia.ia_base.models;

import java.util.ArrayList;
import java.util.List;

public abstract class Inventory {


    private int SKU;
    private String title;
    private String description;
    private Location location;
    private int createdByUserId;
    private InventoryType inventoryType;
    private List<InventoryAttribute> attributes;

    public Inventory() {
        this.attributes = new ArrayList<>();
    }

    public Inventory(int SKU, String title, String description, Location location) {
        this();
        this.SKU = SKU;
        this.title = title;
        this.description = description;
        this.location = location;

    }


    public int getSKU() {
        return SKU;
    }

    public void setSKU(int SKU) {
        this.SKU = SKU;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(InventoryType deviceType) {
        this.inventoryType = deviceType;
    }

    public List<InventoryAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<InventoryAttribute> attributes) {
        this.attributes = attributes != null ? attributes : new ArrayList<>();
    }
    public abstract String getInventoryTypeName();
    public abstract boolean isValid();

    public void moveTo(Location newLocation){
        this.location=newLocation;
    }

    @Override
    public String toString() {
        return "Device{" +
                ", SKU=" + SKU +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", deviceType=" + (inventoryType != null ? inventoryType.getName() : "null") +
                '}';
    }
    /*
    Device types:
    PC +
    Laptop +
    Monitor +
    Projector +
    Smart board +
    Scanner ?
    Keyboard +
    Mouse +
    Printer +
    3D printer +
     */
}

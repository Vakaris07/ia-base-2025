package com.ia.ia_base.models;

public class InventoryAttribute {
    private int id;
    private int inventorySku;
    private AttributeDefinition attribute;
    private String attributeValue;

    public InventoryAttribute() {}

    public InventoryAttribute(int id, int inventorySku, AttributeDefinition attribute, String attributeValue) {
        this.id = id;
        this.inventorySku = inventorySku;
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInventorySku() {
        return inventorySku;
    }

    public void setInventorySku(int inventorySku) {
        this.inventorySku = inventorySku;
    }

    public AttributeDefinition getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeDefinition attribute) {
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return attribute != null ? attribute.getName() + ": " + attributeValue : attributeValue;
    }

}

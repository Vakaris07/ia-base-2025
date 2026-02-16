package com.ia.ia_base.models;


public class ConcreteInventory extends Inventory {

    public ConcreteInventory() {
        super();
    }

    public ConcreteInventory(int SKU, String title, String description, Location location) {
        super(SKU, title, description, location);
    }

    @Override
    public String getInventoryTypeName() {
      InventoryType deviceType = getInventoryType();
      if(deviceType!=null && deviceType.getName()!=null){
          return deviceType.getName();
      }
      return "Device";
    }

    @Override
    public boolean isValid() {
        return getTitle() != null &&
                !getTitle().trim().isEmpty() &&
                getLocation()!=null;
    }
}

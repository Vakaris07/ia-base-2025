package com.ia.ia_base.database.DAO;

import com.ia.ia_base.models.ConcreteInventory;
import com.ia.ia_base.models.Inventory;
import com.ia.ia_base.models.InventoryType;
import com.ia.ia_base.models.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InventoryDAO extends BaseDAO<Inventory> {
    @Override
    protected Inventory mapResultSetToEntity(ResultSet rs) throws SQLException {
        int locationId = rs.getInt("location_id");
        Location location = new Location(locationId, "", "");

        InventoryType inventoryType = null;
        if(rs.getObject("inventory_type_id") != null && !rs.wasNull()){
            inventoryType = new InventoryType();
            inventoryType.setId(rs.getInt("it_id"));
            inventoryType.setName(rs.getString("it_name"));
            inventoryType.setDescription(rs.getString("it_description"));
        }
        int sku = rs.getInt("sku");
        String title = rs.getString("title");
        String description = rs.getString("description");

        Inventory inventory = new ConcreteInventory(sku, title, description, location);
        inventory.setInventoryType(inventoryType);
        if(rs.getObject("created_by_user_id") != null && !rs.wasNull()){
            inventory.setCreatedByUserId(rs.getInt("created_by_user_id"));
        }
        return inventory;
    }

    @Override
    public List<Inventory> findAll() throws SQLException {
        String sql = "SELECT i.*, it.id as it_id, it.name as it_name, it.description as it_description " +
                "FROM inventory i " +
                "LEFT JOIN inventory_types it ON i.inventory_type_id = it.id";
        return executeQuery(sql);
    }

    @Override
    public Inventory findById(int id) throws SQLException {
        String sql = "SELECT i.*, it.id as it_id, it.name as it_name, it.description as it_description " +
                "FROM inventory i " +
                "LEFT JOIN inventory_types it ON i.inventory_type_id = it.id " +
                "WHERE i.sku = ?";
        List<Inventory> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public int create(Inventory entity) throws SQLException {
        String sql = "INSERT INTO inventory (title, description, location_id, created_by_user_id, inventory_type_id) VALUES (?, ?, ?, ?, ?)";

        Integer locationId = null;
        if (entity.getLocation() != null && entity.getLocation().getId() > 0) {
            locationId = entity.getLocation().getId();
        }
        Integer userId = entity.getCreatedByUserId() > 0 ? entity.getCreatedByUserId() : null;

        Integer inventoryTypeId = null;
        if (entity.getInventoryType() != null && entity.getInventoryType().getId() > 0){
            inventoryTypeId = entity.getInventoryType().getId();
        }

        return executeUpdate(sql,
                entity.getTitle(),
                entity.getDescription(),
                locationId,
                userId,
                inventoryTypeId);
    }

    @Override
    public int update(Inventory entity) throws SQLException {
        String sql = "UPDATE inventory SET title = ?, description = ?, location_id = ?, created_by_user_id = ?, inventory_type_id = ? WHERE sku = ?";

        Integer locationId = null;
        if (entity.getLocation() != null && entity.getLocation().getId() > 0) {
            locationId = entity.getLocation().getId();
        }
        Integer userId = entity.getCreatedByUserId() > 0 ? entity.getCreatedByUserId() : null;

        Integer inventoryTypeId = null;
        if (entity.getInventoryType() != null && entity.getInventoryType().getId() > 0){
            inventoryTypeId = entity.getInventoryType().getId();
        }

        return executeUpdate(sql,
                entity.getTitle(),
                entity.getDescription(),
                locationId,
                userId,
                inventoryTypeId,
                entity.getSKU());

    }

    @Override
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM inventory WHERE sku = ?";
        return executeUpdate(sql, id);
    }
}

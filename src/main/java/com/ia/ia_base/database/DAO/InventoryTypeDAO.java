package com.ia.ia_base.database.DAO;

import com.ia.ia_base.models.InventoryType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InventoryTypeDAO extends BaseDAO<InventoryType>{
    @Override
    protected InventoryType mapResultSetToEntity(ResultSet rs) throws SQLException {
        InventoryType entity = new InventoryType(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description")
        );
        return entity;
    }

    @Override
    public List<InventoryType> findAll() throws SQLException {
        String sql = "SELECT * FROM inventory_types ORDER BY name";
        return executeQuery(sql);
    }

    @Override
    public InventoryType findById(int id) throws SQLException {
        String sql = "SELECT * FROM inventory_types WHERE id = ?";
        List<InventoryType> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public int create(InventoryType entity) throws SQLException {
        String sql = "INSERT INTO inventory_types (name, description) VALUES (?, ?)";
        return executeUpdate(sql, entity.getName(), entity.getDescription());
    }

    @Override
    public int update(InventoryType entity) throws SQLException {
        String sql = "UPDATE inventory_types SET name = ?, description = ? WHERE id = ?";
        return executeUpdate(sql, entity.getName(), entity.getDescription(), entity.getId());
    }

    @Override
    public int delete(int id) throws SQLException {
        // First delete all product_type_attribute relationships
        String deleteRelationsSql = "DELETE FROM inventory_type_attributes WHERE inventory_type_id = ?";
        executeUpdate(deleteRelationsSql, id);

        // Then delete the product type
        String sql = "DELETE FROM inventory_types WHERE id = ?";
        return executeUpdate(sql, id);
    }
}

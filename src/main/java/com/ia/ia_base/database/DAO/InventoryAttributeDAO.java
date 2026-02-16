package com.ia.ia_base.database.DAO;

import com.ia.ia_base.models.AttributeDefinition;
import com.ia.ia_base.models.InventoryAttribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InventoryAttributeDAO extends BaseDAO<InventoryAttribute> {
    @Override
    protected InventoryAttribute mapResultSetToEntity(ResultSet rs) throws SQLException {
        InventoryAttribute entity = new InventoryAttribute();
        entity.setId(rs.getInt("id"));
        entity.setInventorySku(rs.getInt("inventory_sku"));
        entity.setAttributeValue(rs.getString("attribute_value"));

        AttributeDefinition attribute = new AttributeDefinition();
        attribute.setId(rs.getInt("attribute_id"));
        attribute.setName(rs.getString("name"));
        attribute.setDataType(rs.getString("data_type"));
        attribute.setDescription(rs.getString("description"));
        entity.setAttribute(attribute);

        return entity;
    }

    public List<InventoryAttribute> findByInventorySku(int sku) throws SQLException{
        String sql = "SELECT ia.*, a.name, a.data_type, a.description " +
                "FROM inventory_attributes ia " +
                "INNER JOIN attributes a ON ia.attribute_id = a.id "+
                "WHERE ia.inventory_sku = ?";
        return executeQuery(sql, sku);
    }

    @Override
    public List<InventoryAttribute> findAll() throws SQLException {
        String sql = "SELECT ia.*, a. name, a.data_type, a.description "+
                "FROM inventory_attributes ia "+
                "INNER JOIN  attributes a ON ia.attribute_id = a.id";
        return executeQuery(sql);
    }

    @Override
    public InventoryAttribute findById(int id) throws SQLException {
        String sql = "SELECT ia.*, a. name, a.data_type, a.description "+
                "FROM inventory_attributes ia "+
                "INNER JOIN  attributes a ON ia.attribute_id = a.id " +
                "WHERE ia.id = ?";
        List<InventoryAttribute> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public int create(InventoryAttribute entity) throws SQLException {
        String sql = "INSERT INTO inventory_attributes (inventory_sku, attribute_id, attribute_value) VALUES (?, ?, ?)";
        int attributeId = entity.getAttribute() !=null ? entity.getAttribute().getId() : 0;
        return executeUpdate(sql, entity.getInventorySku(), attributeId, entity.getAttributeValue());
    }

    @Override
    public int update(InventoryAttribute entity) throws SQLException {
        String sql = "UPDATE inventory_attributes SET attribute_value = ? WHERE id = ?";
        return executeUpdate(sql, entity.getAttributeValue(), entity.getId());

    }

    public int deleteByInventorySku(int sku) throws SQLException{
        String sql = "DELETE FROM inventory_attributes WHERE inventory_sku = ?";
        return executeUpdate(sql, sku);
    }

    @Override
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM inventory_attributes WHERE id = ?";
        return executeUpdate(sql, id);
    }

    public int deleteByInventoryAndAttribute(int inventorySku, int attributeId) throws SQLException{
        String sql = "DELETE FROM inventory_attributes WHERE inventory_sku = ? AND attribute_id = ?";
        return executeUpdate(sql, inventorySku, attributeId);
    }
}

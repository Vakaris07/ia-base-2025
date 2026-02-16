package com.ia.ia_base.database.DAO;

import com.ia.ia_base.models.AttributeDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AttributeDAO extends BaseDAO<AttributeDefinition> {


    @Override
    protected AttributeDefinition mapResultSetToEntity(ResultSet rs) throws SQLException {
        AttributeDefinition entity = new AttributeDefinition(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("data_type"),
                rs.getString("description")
        );
        return entity;

    }
    @Override
    public List<AttributeDefinition> findAll() throws SQLException {
        String sql = "SELECT * FROM attributes ORDER BY name";
        return executeQuery(sql);
    }


    @Override
    public AttributeDefinition findById(int id) throws SQLException {
        String sql = "SELECT * FROM attributes WHERE id = ?";
        List<AttributeDefinition> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }
    @Override
    public int create(AttributeDefinition entity) throws SQLException {
        String sql = "INSERT INTO attributes (name, data_type, description) VALUES (?, ?, ?)";
        return executeUpdate(sql, entity.getName(), entity.getDataType(), entity.getDescription());
    }

    @Override
    public int update(AttributeDefinition entity) throws SQLException {
        String sql = "UPDATE attributes SET name = ?, data_type = ?, description = ? WHERE id = ?";
        return executeUpdate(sql, entity.getName(), entity.getDataType(), entity.getDescription(), entity.getId());
    }

    @Override
    public int delete(int id) throws SQLException {
        // First delete all product_attribute relationships
        String deleteRelationsSql = "DELETE FROM inventory_attributes WHERE attribute_id = ?";
        executeUpdate(deleteRelationsSql, id);

        // Then delete the attribute
        String sql = "DELETE FROM attributes WHERE id = ?";
        return executeUpdate(sql, id);
    }
}

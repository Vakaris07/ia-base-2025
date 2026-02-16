package com.ia.ia_base.database.example;

import com.ia.ia_base.database.DAO.BaseDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Example DAO class showing how to use BaseDAO.
 * 
 * This example shows how to create your own DAO class for working with database.
 */
public class ExampleDAO extends BaseDAO<ExampleEntity> {
    
    /**
     * Gets all records from table
     */
    public List<ExampleEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM example_table";
        return executeQuery(sql);
    }
    
    /**
     * Gets record by ID
     */
    public ExampleEntity findById(int id) throws SQLException {
        String sql = "SELECT * FROM example_table WHERE id = ?";
        List<ExampleEntity> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Creates new record
     */
    public int create(ExampleEntity entity) throws SQLException {
        String sql = "INSERT INTO example_table (name, description) VALUES (?, ?)";
        return executeUpdate(sql, entity.getName(), entity.getDescription());
    }
    
    /**
     * Updates existing record
     */
    public int update(ExampleEntity entity) throws SQLException {
        String sql = "UPDATE example_table SET name = ?, description = ? WHERE id = ?";
        return executeUpdate(sql, entity.getName(), entity.getDescription(), entity.getId());
    }
    
    /**
     * Deletes record by ID
     */
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM example_table WHERE id = ?";
        return executeUpdate(sql, id);
    }
    
    @Override
    protected ExampleEntity mapResultSetToEntity(ResultSet rs) throws SQLException {
        ExampleEntity entity = new ExampleEntity();
        entity.setId(rs.getInt("id"));
        entity.setName(rs.getString("name"));
        entity.setDescription(rs.getString("description"));
        return entity;
    }
}


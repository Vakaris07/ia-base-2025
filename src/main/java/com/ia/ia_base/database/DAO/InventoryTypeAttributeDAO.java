package com.ia.ia_base.database.DAO;

import com.ia.ia_base.database.DatabaseConnection;
import com.ia.ia_base.models.AttributeDefinition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryTypeAttributeDAO{
    private DatabaseConnection dbConnection;

    public InventoryTypeAttributeDAO(){
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<AttributeDefinition> findAttributesByInventoryTypeId(int inventoryTypeId) throws SQLException {
        String sql = "SELECT a.* FROM attributes a " +
                "INNER JOIN inventory_type_attributes ita ON a.id = ita.attribute_id " +
                "WHERE ita.inventory_type_id = ? " +
                "ORDER BY a.name";
        List<AttributeDefinition> attributes = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventoryTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttributeDefinition attribute = new AttributeDefinition();
                    attribute.setId(rs.getInt("id"));
                    attribute.setName(rs.getString("name"));
                    attribute.setDataType(rs.getString("data_type"));
                    attribute.setDescription(rs.getString("description"));
                    attributes.add(attribute);
                }
            }
        }
        return attributes;
    }
    public int addAttributeToInventoryType(int inventoryTypeId, int attributeId) throws SQLException {
        String sql = "INSERT INTO inventory_type_attributes (inventory_type_id, attribute_id) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inventoryTypeId);
            stmt.setInt(2, attributeId);
            return stmt.executeUpdate();
        }
    }

    public int removeAttributeFromInventoryType(int inventoryTypeId, int attributeId) throws SQLException{
        String sql = "DELETE FROM inventory_type_attributes WHERE inventory_type = ? AND attribute_id = ?";
        try(Connection con = dbConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement(sql)){
            stmt.setInt(1, inventoryTypeId);
            stmt.setInt(2, attributeId);
            return stmt.executeUpdate();
        }
    }
    public int removeAllAttributesFromProductType(int inventoryTypeID) throws SQLException {
        String sql = "DELETE FROM inventory_type_attributes WHERE inventory_type_id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, inventoryTypeID);
            return stmt.executeUpdate();
        }
    }
}

package com.ia.ia_base.database.DAO;

import com.ia.ia_base.models.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class LocationDAO extends BaseDAO<Location> {
    @Override
    protected Location mapResultSetToEntity(ResultSet rs) throws SQLException {
        Location entity = new Location(rs.getInt("id"),rs.getString("school_sector"), rs.getString("room_number"));
        return entity;
    }

    @Override
    public List<Location> findAll() throws SQLException {
        String sql = "SELECT * FROM locations";
        return executeQuery(sql);
    }

    @Override
    public Location findById(int id) throws SQLException {
        String sql = "SELECT * FROM locations WHERE id = ?";
        List<Location> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public int create(Location entity) throws SQLException {
        String sql = "INSERT INTO locations (school_sector, room_number) VALUES (?, ?)";
        return executeUpdate(sql, entity.getSchoolSector(), entity.getRoomNumber());
    }

    @Override
    public int update(Location entity) throws SQLException {
        String sql = "UPDATE locations SET school_sector = ?, room_number = ? WHERE id = ?";
        return executeUpdate(sql, entity.getSchoolSector(),entity.getRoomNumber(), entity.getId());
    }

    @Override
    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM locations WHERE id = ?";
        return executeUpdate(sql, id);
    }
}

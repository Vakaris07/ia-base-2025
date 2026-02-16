package com.ia.ia_base.database.DAO;

import com.ia.ia_base.models.AdminUser;
import com.ia.ia_base.models.RegisteredUser;
import com.ia.ia_base.models.Role;
import com.ia.ia_base.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDAO extends BaseDAO<User> {

    public List<User> findAll() throws SQLException {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u" +
                "LEFT JOIN roles r ON u.role_id = r.id";

        return executeQuery(sql);
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u" +
                "LEFT JOIN roles r ON u.role_id = r.id WHERE u.id = ?";

        List<User> results = executeQuery(sql, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.id as role_id, r.name as role_name FROM users u " +
                "LEFT JOIN roles r ON u.role_id = r.id WHERE u.email = ?";
        List<User> results = executeQuery(sql, email);
        return results.isEmpty() ? null : results.get(0);
    }

    public int create(User entity) throws SQLException {
        String sql = "INSERT INTO users(email, password_hash, role_id, is_blocked, must_change_password) VALUES(?, ?, ?, ?, ?)";
        Integer roleId = entity.getRole() != null && entity.getRole().getId() > 0 ?
                entity.getRole().getId() : null;
        return executeUpdate(sql, entity.getEmail(), entity.getPasswordHash(), roleId, entity.isBlocked(), entity.isMustChangePassword());
    }

    public int update(User entity) throws SQLException {
        String sql = "UPDATE users SET email = ?, password_hash = ?, role_id = ?, " +
                "is_blocked = ?, must_change_password = ? WHERE id = ?";
        Integer roleId = entity.getRole() != null && entity.getRole().getId() > 0 ?
                entity.getRole().getId() : null;
        return executeUpdate(sql, entity.getEmail(), entity.getPasswordHash(), roleId, entity.isBlocked(), entity.isMustChangePassword(), entity.getId());
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        return executeUpdate(sql, id);
    }

    public int blockUser(int id) throws SQLException {
        String sql = "UPDATE users SET is_blocked = true WHERE id = ?";
        return executeUpdate(sql, id);
    }

    public int unblockUser(int id) throws SQLException {
        String sql = "UPDATE users SET is_blocked = false WHERE id = ?";
        return executeUpdate(sql, id);
    }

    public int resetPassword(int id, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, must_change_password = true WHERE id = ?";
        return executeUpdate(sql, newPasswordHash, id);
    }


    @Override
    protected User mapResultSetToEntity(ResultSet rs) throws SQLException {
        User user;
        String roleName = null;
        Role role = null;

        //Load role from database if it exists
        if (rs.getObject("role_id") != null && !rs.wasNull()) {
            role = new Role(rs.getInt("role_id"), rs.getString("role_name"));
            roleName = role.getName();
        }

        //Create users based on role
        if (roleName != null && "administrator".equalsIgnoreCase(roleName)) {
            user = new AdminUser();
        } else {
            user = new RegisteredUser();
        }

        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setMustChangePassword(rs.getBoolean("must_change_password"));
        user.setRole(role);

        return user;
    }
}

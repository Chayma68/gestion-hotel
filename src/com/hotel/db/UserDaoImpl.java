package com.hotel.db;

import com.hotel.model.Client;
import com.hotel.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private final ClientDao clientDao = new ClientDaoImpl();

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToUser(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.findById(): " + e.getMessage(), e);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToUser(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.findByUsername(): " + e.getMessage(), e);
        }
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToUser(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.findByUsernameAndPassword(): " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.findAll(): " + e.getMessage(), e);
        }
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO user (username, password, role, client_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());

            if (user.getClient() != null) {
                ps.setInt(4, user.getClient().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.save(): " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE user SET username=?, password=?, role=?, client_id=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());

            if (user.getClient() != null) {
                ps.setInt(4, user.getClient().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setInt(5, user.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.update(): " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL User.delete(): " + e.getMessage(), e);
        }
    }

    // --------------------------
    // Mapping SQL -> User
    // --------------------------
    private User mapRowToUser(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        User.Role role = User.Role.valueOf(rs.getString("role"));

        int clientId = rs.getInt("client_id");
        Client client = null;

        // si le user est un client, on charge l'objet Client
        if (!rs.wasNull() && clientId > 0) {

            for (Client c : clientDao.findAll()) {
                if (c.getId() == clientId) {
                    client = c;
                    break;
                }
            }
        }

        if (client != null) {
            return new User(id, username, password, role, client);
        }
        return new User(id, username, password, role);
    }
}

package com.hotel.db;

import com.hotel.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDaoImpl implements ClientDao {

    @Override
    public Client add(Client client) {
        String sql = "INSERT INTO client(name, contact, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getContact());
            ps.setString(3, client.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    client.setId(rs.getInt(1));
                }
            }
            return client;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du client", e);
        }
    }

    @Override
    public List<Client> findAll() {
        String sql = "SELECT id, name, contact, email FROM client";
        List<Client> result = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Client c = new Client();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setContact(rs.getString("contact"));
                c.setEmail(rs.getString("email"));
                result.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des clients", e);
        }

        return result;
    }

    @Override
    public Client findByName(String name) {
        String sql = "SELECT id, name, contact, email FROM client WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Client c = new Client();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setContact(rs.getString("contact"));
                    c.setEmail(rs.getString("email"));
                    return c;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du client", e);
        }
        return null;
    }
}

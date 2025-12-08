package com.hotel.db;

import com.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDaoImpl implements RoomDao {

    @Override
    public Room addRoom(Room room) {
        String sql = "INSERT INTO room (room_number, type, price, available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, room.getNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setBoolean(4, room.isAvailable());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setId(rs.getInt(1));
                }
            }
            return room;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la chambre", e);
        }
    }

    @Override
    public void updateRoom(Room room) {
        String sql = "UPDATE room SET room_number = ?, type = ?, price = ?, available = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, room.getNumber());
            ps.setString(2, room.getType());
            ps.setDouble(3, room.getPrice());
            ps.setBoolean(4, room.isAvailable());
            ps.setInt(5, room.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la chambre", e);
        }
    }

    @Override
    public void deleteRoom(int roomId) {
        String sql = "DELETE FROM room WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la chambre", e);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        String sql = "SELECT id, room_number, type, price, available FROM room";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setNumber(rs.getString("room_number"));
                room.setType(rs.getString("type"));
                room.setPrice(rs.getDouble("price"));
                room.setAvailable(rs.getBoolean("available"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des chambres", e);
        }
        return rooms;
    }

    @Override
    public List<Room> getAvailableRooms() {
        String sql = "SELECT id, room_number, type, price, available FROM room WHERE available = true";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setNumber(rs.getString("room_number"));
                room.setType(rs.getString("type"));
                room.setPrice(rs.getDouble("price"));
                room.setAvailable(rs.getBoolean("available"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des chambres disponibles", e);
        }
        return rooms;
    }

    @Override
    public Room getRoomById(int id) {
        String sql = "SELECT id, room_number, type, price, available FROM room WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setNumber(rs.getString("room_number"));
                    room.setType(rs.getString("type"));
                    room.setPrice(rs.getDouble("price"));
                    room.setAvailable(rs.getBoolean("available"));
                    return room;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la chambre", e);
        }
        return null;
    }
}

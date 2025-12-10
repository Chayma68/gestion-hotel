package com.hotel.db;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ReservationDaoImpl implements ReservationDao {

    @Override
    public Reservation save(Reservation reservation) {
        String sql = "INSERT INTO reservation (client_id, room_id, check_in, check_out, confirmed) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reservation.getClient().getId());
            ps.setInt(2, reservation.getRoom().getId());
            ps.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            ps.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            ps.setBoolean(5, reservation.isConfirmed());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reservation.setId(rs.getInt(1));
                }
            }
            return reservation;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la réservation", e);
        }
    }

    @Override
    public List<Reservation> findAll() {
        String sql =
                "SELECT r.id               AS r_id, " +
                        "       r.check_in         AS r_check_in, " +
                        "       r.check_out        AS r_check_out, " +
                        "       r.confirmed        AS r_confirmed, " +
                        "       c.id               AS c_id, " +
                        "       c.name             AS c_name, " +
                        "       c.contact          AS c_contact, " +
                        "       c.email            AS c_email, " +
                        "       room.id            AS room_id, " +
                        "       room.room_number   AS room_number, " +
                        "       room.type          AS room_type, " +
                        "       room.price         AS room_price, " +
                        "       room.available     AS room_available " +
                        "FROM reservation r " +
                        "JOIN client c ON r.client_id = c.id " +
                        "JOIN room   ON r.room_id   = room.id";

        List<Reservation> result = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des réservations", e);
        }
        return result;
    }

    @Override
    public Reservation findById(int id) {
        String sql =
                "SELECT r.id               AS r_id, " +
                        "       r.check_in         AS r_check_in, " +
                        "       r.check_out        AS r_check_out, " +
                        "       r.confirmed        AS r_confirmed, " +
                        "       c.id               AS c_id, " +
                        "       c.name             AS c_name, " +
                        "       c.contact          AS c_contact, " +
                        "       c.email            AS c_email, " +
                        "       room.id            AS room_id, " +
                        "       room.room_number   AS room_number, " +
                        "       room.type          AS room_type, " +
                        "       room.price         AS room_price, " +
                        "       room.available     AS room_available " +
                        "FROM reservation r " +
                        "JOIN client c ON r.client_id = c.id " +
                        "JOIN room   ON r.room_id   = room.id " +
                        "WHERE r.id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la réservation", e);
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la réservation", e);
        }
    }

    @Override
    public void update(Reservation reservation) {
        String sql = "UPDATE reservation " +
                "SET client_id = ?, room_id = ?, check_in = ?, check_out = ?, confirmed = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservation.getClient().getId());
            ps.setInt(2, reservation.getRoom().getId());
            ps.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            ps.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            ps.setBoolean(5, reservation.isConfirmed());
            ps.setInt(6, reservation.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la réservation", e);
        }
    }

    // --------- utilitaire de mapping ---------

    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        // Client
        Client client = new Client();
        client.setId(rs.getInt("c_id"));
        client.setName(rs.getString("c_name"));
        client.setContact(rs.getString("c_contact"));
        client.setEmail(rs.getString("c_email"));

        // Room
        Room room = new Room();
        room.setId(rs.getInt("room_id"));
        room.setNumber(rs.getString("room_number"));
        room.setType(rs.getString("room_type"));
        room.setPrice(rs.getDouble("room_price"));
        room.setAvailable(rs.getBoolean("room_available"));

        // Reservation
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("r_id"));
        reservation.setClient(client);
        reservation.setRoom(room);

        LocalDate checkIn = rs.getDate("r_check_in").toLocalDate();
        LocalDate checkOut = rs.getDate("r_check_out").toLocalDate();

        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setConfirmed(rs.getBoolean("r_confirmed"));

        return reservation;
    }
}

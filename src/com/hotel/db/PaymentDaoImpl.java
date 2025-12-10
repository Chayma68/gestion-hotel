package com.hotel.db;

import com.hotel.model.Client;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PaymentDaoImpl implements PaymentDao {

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payment (reservation_id, amount, payment_date, paid) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getReservation().getId());
            ps.setDouble(2, payment.getAmount());
            ps.setDate(3, Date.valueOf(payment.getDate()));
            ps.setBoolean(4, payment.isPaid());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                }
            }

            return payment;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du paiement", e);
        }
    }

    @Override
    public List<Payment> findByClientId(int clientId) {
        List<Payment> result = new ArrayList<>();

        String sql =
                "SELECT p.id                AS p_id, " +
                        "       p.amount           AS p_amount, " +
                        "       p.payment_date     AS p_date, " +
                        "       p.paid             AS p_paid, " +
                        "       r.id               AS r_id, " +
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
                        "FROM payment p " +
                        "JOIN reservation r ON p.reservation_id = r.id " +
                        "JOIN client c ON r.client_id = c.id " +
                        "JOIN room ON r.room_id = room.id " +
                        "WHERE c.id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, clientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
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
                    reservation.setCheckInDate(rs.getDate("r_check_in").toLocalDate());
                    reservation.setCheckOutDate(rs.getDate("r_check_out").toLocalDate());
                    reservation.setConfirmed(rs.getBoolean("r_confirmed"));

                    // Payment
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("p_id"));
                    payment.setClient(client);
                    payment.setReservation(reservation);
                    payment.setAmount(rs.getDouble("p_amount"));
                    payment.setDate(rs.getDate("p_date").toLocalDate());
                    payment.setPaid(rs.getBoolean("p_paid"));

                    result.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des paiements du client", e);
        }

        return result;
    }

    @Override
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total FROM payment";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du chiffre d'affaires", e);
        }
    }
}

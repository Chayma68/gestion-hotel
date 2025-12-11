package com.hotel.db;

import com.hotel.model.Invoice;
import com.hotel.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDaoImpl implements InvoiceDao {

    private final ReservationDao reservationDao = new ReservationDaoImpl();

    @Override
    public Invoice save(Invoice invoice) {
        String sql = "INSERT INTO invoice (reservation_id, invoice_date, total_amount) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getReservation().getId());
            ps.setDate(2, Date.valueOf(invoice.getDate()));
            ps.setDouble(3, invoice.getTotalAmount());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    invoice.setId(rs.getInt(1));
                }
            }

            return invoice;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la facture", e);
        }
    }

    @Override
    public Invoice findById(int id) {
        String sql = "SELECT id, reservation_id, invoice_date, total_amount FROM invoice WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la facture", e);
        }
        return null;
    }

    @Override
    public Invoice findByReservationId(int reservationId) {
        String sql = "SELECT id, reservation_id, invoice_date, total_amount FROM invoice WHERE reservation_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToInvoice(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la facture par réservation", e);
        }
        return null;
    }

    @Override
    public List<Invoice> findAll() {
        String sql = "SELECT id, reservation_id, invoice_date, total_amount FROM invoice";
        List<Invoice> result = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRowToInvoice(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des factures", e);
        }

        return result;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM invoice WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la facture", e);
        }
    }

    // ---------- mapping utilitaire ----------

    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        int invoiceId = rs.getInt("id");
        int reservationId = rs.getInt("reservation_id");
        LocalDate date = rs.getDate("invoice_date").toLocalDate();
        double total = rs.getDouble("total_amount");

        Reservation reservation = reservationDao.findById(reservationId);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setReservation(reservation);
        invoice.setDate(date);
        invoice.setTotalAmount(total);

        return invoice;
    }
}

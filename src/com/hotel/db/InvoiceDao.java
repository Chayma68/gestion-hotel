package com.hotel.db;

import com.hotel.model.Invoice;

import java.util.List;

public interface InvoiceDao {

    Invoice save(Invoice invoice);

    Invoice findById(int id);

    Invoice findByReservationId(int reservationId);

    List<Invoice> findAll();

    void delete(int id);
}

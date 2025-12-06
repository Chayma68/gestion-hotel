package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents an invoice for a particular reservation.  It includes
 * details such as the reservation itself, the date of invoice
 * generation and the total amount due.  In a real application an
 * invoice might contain additional line items, taxes and so on, but
 * for the purposes of this project it is kept simple.
 */
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Reservation reservation;
    private LocalDate date;
    private double total;

    public Invoice(int id, Reservation reservation, LocalDate date, double total) {
        this.id = id;
        this.reservation = reservation;
        this.date = date;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", reservation=" + (reservation != null ? reservation.getId() : null) +
                ", date=" + date +
                ", total=" + total +
                '}';
    }
}
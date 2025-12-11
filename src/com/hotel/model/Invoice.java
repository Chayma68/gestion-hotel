package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Reservation reservation;
    private LocalDate date;
    private double totalAmount;;

    public Invoice(int id, Reservation reservation, LocalDate date, double total) {
        this.id = id;
        this.reservation = reservation;
        this.date = date;
        this.totalAmount = total;
    }

    public Invoice() {

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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double total) {
        this.totalAmount = total;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", reservation=" + (reservation != null ? reservation.getId() : null) +
                ", date=" + date +
                ", total=" + totalAmount +
                '}';
    }
}
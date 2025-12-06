package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a payment made by a client for a reservation.  The
 * payment records the associated reservation, the amount paid and the
 * date.  Payments can be marked as paid to indicate whether the
 * transaction has been completed.  This class is serialisable for
 * transport across remote interfaces.
 */
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Client client;
    private Reservation reservation;
    private double amount;
    private LocalDate date;
    private boolean paid;

    public Payment(int id, Client client, Reservation reservation, double amount, LocalDate date, boolean paid) {
        this.id = id;
        this.client = client;
        this.reservation = reservation;
        this.amount = amount;
        this.date = date;
        this.paid = paid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", client=" + (client != null ? client.getName() : null) +
                ", reservation=" + (reservation != null ? reservation.getId() : 0) +
                ", amount=" + amount +
                ", date=" + date +
                ", paid=" + paid +
                '}';
    }
}
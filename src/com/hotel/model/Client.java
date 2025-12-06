package com.hotel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client of the hotel.  A client has a unique identifier,
 * a full name, some contact information (phone or address) and an
 * optional e‑mail address.  The client maintains a list of
 * reservations to simplify access to a client’s booking history.  This
 * class is serialisable so it can be sent over RMI if desired.
 */
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String contact;
    private String email;
    private final List<Reservation> reservations;

    public Client(int id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.reservations = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * Adds a reservation to the client’s history.  The reservation is not
     * duplicated in the list if it already exists.
     *
     * @param reservation reservation to add
     */
    public void addReservation(Reservation reservation) {
        if (!reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
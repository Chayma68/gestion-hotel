package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;


public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Client client;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private boolean confirmed;

    public Reservation(int id, Client client, Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = id;
        this.client = client;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.confirmed = false;
    }

    public Reservation() {

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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", client=" + (client != null ? client.getName() : null) +
                ", room=" + (room != null ? room.getId() : 0) +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", confirmed=" + confirmed +
                '}';
    }
}
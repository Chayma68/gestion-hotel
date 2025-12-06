package com.hotel.model;

import java.io.Serializable;

/**
 * Represents a room in the hotel.  Each room has an identifier,
 * a type (for example single, double, suite), a nightly price and
 * a flag indicating whether it is currently available.  Instances
 * of this class are serialisable so they can be passed through RMI
 * calls if required.
 */
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String type;
    private double price;
    private boolean available;

    public Room(int id, String type, double price, boolean available) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }
}
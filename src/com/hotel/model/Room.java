package com.hotel.model;

import java.io.Serializable;

public class Room implements Serializable {

    private int id;
    private String number;
    private String type;
    private double price;
    private boolean available;

    public Room() {
    }
    public Room(int id, String type, double price, boolean available) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.available = available;
        this.number = null;
    }


    public Room(int id, String number, String type, double price, boolean available) {
        this.id = id;
        this.number = number;
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

    public String getNumber() {        // ✅ utilisé dans RoomDaoImpl
        return number;
    }

    public void setNumber(String number) {   // ✅ utilisé dans RoomDaoImpl
        this.number = number;
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
        String num = (number != null && !number.isEmpty()) ? number + " - " : "";
        return num + type + " (" + price + " €/nuit)";
    }

}

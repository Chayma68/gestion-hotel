package com.hotel.model;

import java.io.Serializable;


public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String contact;
    private String email;


    public Client(){} ;

    public Client(int id, String name, String contact, String email) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.email = email;

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



    @Override
    public String toString() {
        if (email != null && !email.isEmpty()) {
            return name + " (" + email + ")";
        }
        return name;
    }

}
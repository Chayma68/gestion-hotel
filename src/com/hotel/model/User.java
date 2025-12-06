package com.hotel.model;

import java.io.Serializable;

/**
 * Represents a user of the system.  Users may either be employees or
 * clients.  Each user has a username, password and role.  When the
 * role is CLIENT the {@link #client} field links to the underlying
 * client details.  This class is serialisable for transmission via
 * remote interfaces if required.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role {
        EMPLOYEE,
        CLIENT
    }

    private int id;
    private String username;
    private String password;
    private Role role;
    private Client client;

    public User(int id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(int id, String username, String password, Role role, Client client) {
        this(id, username, password, role);
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
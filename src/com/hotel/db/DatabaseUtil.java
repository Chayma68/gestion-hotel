package com.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/gestion_hotel?useSSL=false&serverTimezone=UTC";
    private static final String USER = "hotel_user";
    private static final String PASSWORD = "hotel_pwd";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // charge le driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Impossible de charger le driver MySQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

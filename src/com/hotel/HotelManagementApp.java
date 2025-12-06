package com.hotel;

import com.hotel.model.Client;
import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ReportServiceImpl;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.ejb.PaymentServiceImpl;
import com.hotel.service.rmi.HotelService;
import com.hotel.service.rmi.HotelServiceImpl;
import com.hotel.ui.LoginFrame;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entry point for the hotel management application.  This class
 * instantiates the service implementations, populates a list of
 * sample users and displays the login window.  The user’s time zone
 * and locale are not explicitly set; Swing defaults are used.
 */
public class HotelManagementApp {
    public static void main(String[] args) {
        // Instantiate services
        try {
            HotelService hotelService = new HotelServiceImpl();
            PaymentService paymentService = new PaymentServiceImpl();
            ReportService reportService = new ReportServiceImpl(hotelService, paymentService);

            // Create sample users
            List<User> users = new ArrayList<>();
            // Add an employee
            users.add(new User(1, "admin", "admin", User.Role.EMPLOYEE));
            // Link clients to user accounts
            List<Client> clients = hotelService.getAllClients();
            int userIdCounter = 2;
            for (Client c : clients) {
                String username = c.getName().toLowerCase();
                String password = "password";
                users.add(new User(userIdCounter++, username, password, User.Role.CLIENT, c));
            }

            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(hotelService, paymentService, reportService, users);
                loginFrame.setVisible(true);
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
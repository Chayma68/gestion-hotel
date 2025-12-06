package com.hotel.ui;

import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window for hotel employees.  It contains a tabbed
 * interface exposing panels for room, client and reservation
 * management, payment processing and reporting.  The window title
 * includes the logged in employee’s username for clarity.
 */
public class EmployeeUI extends JFrame {
    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final User user;

    public EmployeeUI(HotelService hotelService, PaymentService paymentService, ReportService reportService, User user) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.user = user;
        initialiseUI();
    }

    private void initialiseUI() {
        setTitle("Hotel Management - Employee: " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Rooms", new RoomManagementPanel(hotelService));
        tabbedPane.addTab("Clients", new ClientManagementPanel(hotelService));
        tabbedPane.addTab("Reservations", new ReservationManagementPanel(hotelService));
        tabbedPane.addTab("Payments", new PaymentPanel(hotelService, paymentService));
        tabbedPane.addTab("Reports", new ReportPanel(hotelService, reportService));

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
}
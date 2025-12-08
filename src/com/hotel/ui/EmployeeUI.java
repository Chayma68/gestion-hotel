package com.hotel.ui;

import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import java.awt.*;

public class EmployeeUI extends JFrame {

    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final User employee;

    public EmployeeUI(HotelService hotelService,
                      PaymentService paymentService,
                      ReportService reportService,
                      User employee) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.employee = employee;

        initUI();
    }

    private void initUI() {
        setTitle("Gestion d'hôtel - Espace employé");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // ----- Barre supérieure -----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Espace employé - Gestion d'hôtel");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));

        JLabel userLabel = new JLabel("Connecté : " + employee.getUsername());
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(userLabel, BorderLayout.EAST);

        // ----- Onglets -----
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabs.addTab("Chambres", new RoomManagementPanel(hotelService));
        tabs.addTab("Clients", new ClientManagementPanel(hotelService));
        tabs.addTab("Réservations", new ReservationManagementPanel(hotelService));
        tabs.addTab("Paiements", new PaymentPanel(hotelService, paymentService));
        tabs.addTab("Rapports", new ReportPanel(hotelService, reportService));

        // ----- Layout principal -----
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topBar, BorderLayout.NORTH);
        getContentPane().add(tabs, BorderLayout.CENTER);
    }
}

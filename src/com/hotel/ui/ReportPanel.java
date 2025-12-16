package com.hotel.ui;

import com.hotel.model.Client;
import com.hotel.service.ReportService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;


public class ReportPanel extends JPanel {
    private final HotelService hotelService;
    private final ReportService reportService;
    private JTextArea reportArea;

    public ReportPanel(HotelService hotelService, ReportService reportService) {
        this.hotelService = hotelService;
        this.reportService = reportService;
        initialiseUI();
    }

    private void initialiseUI() {
        setLayout(new BorderLayout());
        reportArea = new JTextArea(15, 50);
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton occupancyButton = new JButton("Occupancy Report");
        JButton revenueButton = new JButton("Revenue Report");
        JButton clientHistoryButton = new JButton("Client History Report");
        buttonsPanel.add(occupancyButton);
        buttonsPanel.add(revenueButton);
        buttonsPanel.add(clientHistoryButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.NORTH);

        occupancyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportArea.setText(reportService.generateOccupancyReport());
            }
        });

        revenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reportArea.setText(reportService.generateRevenueReport());
            }
        });

        clientHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    List<Client> clients = hotelService.getAllClients();
                    if (clients.isEmpty()) {
                        JOptionPane.showMessageDialog(ReportPanel.this, "No clients available.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    Client selected = (Client) JOptionPane.showInputDialog(
                            ReportPanel.this,
                            "Select a client:",
                            "Client History",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            clients.toArray(),
                            clients.get(0)
                    );
                    if (selected != null) {
                        reportArea.setText(reportService.generateClientHistoryReport(selected));
                    }
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(ReportPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
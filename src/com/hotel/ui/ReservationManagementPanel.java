package com.hotel.ui;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationManagementPanel extends JPanel {
    private final HotelService hotelService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReservationManagementPanel(HotelService hotelService) {
        this.hotelService = hotelService;
        initialiseUI();
        refreshTable();
    }

    private void initialiseUI() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Client", "Room", "Check-In", "Check-Out", "Confirmed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Make Reservation");
        JButton confirmButton = new JButton("Confirm Reservation");
        JButton cancelButton = new JButton("Cancel Reservation");
        JButton refreshButton = new JButton("Refresh");
        buttonsPanel.add(addButton);
        buttonsPanel.add(confirmButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReservationDialog();
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
                    try {
                        hotelService.confirmReservation(reservationId);
                        refreshTable();
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(ReservationManagementPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ReservationManagementPanel.this, "Please select a reservation to confirm.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int reservationId = (int) tableModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(ReservationManagementPanel.this, "Are you sure you want to cancel this reservation?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            hotelService.cancelReservation(reservationId);
                            refreshTable();
                        } catch (RemoteException ex) {
                            JOptionPane.showMessageDialog(ReservationManagementPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ReservationManagementPanel.this, "Please select a reservation to cancel.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        refreshButton.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        try {
            List<Reservation> reservations = hotelService.getAllReservations();
            tableModel.setRowCount(0);
            for (Reservation r : reservations) {
                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getClient() != null ? r.getClient().getName() : "",
                        r.getRoom() != null ? r.getRoom().getId() : "",
                        r.getCheckInDate(),
                        r.getCheckOutDate(),
                        r.isConfirmed() ? "Yes" : "No"
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReservationDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Make Reservation", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            // Fetch lists for selection
            List<Client> clients = hotelService.getAllClients();
            List<Room> availableRooms = hotelService.getAvailableRooms();
            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No clients available. Please add a client first.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (availableRooms.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available rooms. Please add rooms or wait until a room becomes available.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JLabel clientLabel = new JLabel("Client:");
            JComboBox<Client> clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
            JLabel roomLabel = new JLabel("Room:");
            JComboBox<Room> roomCombo = new JComboBox<>(availableRooms.toArray(new Room[0]));
            JLabel checkInLabel = new JLabel("Check-in (YYYY-MM-DD):");
            JTextField checkInField = new JTextField(20);
            JLabel checkOutLabel = new JLabel("Check-out (YYYY-MM-DD):");
            JTextField checkOutField = new JTextField(20);

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(clientLabel, gbc);
            gbc.gridx = 1;
            panel.add(clientCombo, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(roomLabel, gbc);
            gbc.gridx = 1;
            panel.add(roomCombo, gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(checkInLabel, gbc);
            gbc.gridx = 1;
            panel.add(checkInField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            panel.add(checkOutLabel, gbc);
            gbc.gridx = 1;
            panel.add(checkOutField, gbc);

            JButton okButton = new JButton("Reserve");
            JButton cancelButton = new JButton("Cancel");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String checkInText = checkInField.getText().trim();
                    String checkOutText = checkOutField.getText().trim();
                    LocalDate checkIn;
                    LocalDate checkOut;
                    try {
                        checkIn = LocalDate.parse(checkInText);
                        checkOut = LocalDate.parse(checkOutText);
                        if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
                            JOptionPane.showMessageDialog(dialog, "Check-out date must be after check-in date.", "Validation", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (DateTimeParseException ex) {
                        JOptionPane.showMessageDialog(dialog, "Please enter valid dates in YYYY-MM-DD format.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Client client = (Client) clientCombo.getSelectedItem();
                    Room room = (Room) roomCombo.getSelectedItem();
                    try {
                        Reservation res = hotelService.makeReservation(client, room, checkIn, checkOut);
                        if (res != null) {
                            JOptionPane.showMessageDialog(dialog, "Reservation created successfully (ID: " + res.getId() + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshTable();
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Reservation could not be created. Room may be unavailable.", "Failure", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            cancelButton.addActionListener(e -> dialog.dispose());
            dialog.getContentPane().add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
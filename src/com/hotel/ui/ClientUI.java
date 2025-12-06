package com.hotel.ui;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ejb.PaymentService;
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

/**
 * Main application window for hotel clients.  Provides a simplified
 * interface that allows the client to search and book available
 * rooms and review their own reservations.  Clients may also
 * generate invoices and process payments from within the interface.
 */
public class ClientUI extends JFrame {
    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final User user;

    // Components for available rooms
    private JTable availableTable;
    private DefaultTableModel availableModel;
    private JTextField checkInField;
    private JTextField checkOutField;

    // Components for my reservations
    private JTable myResTable;
    private DefaultTableModel myResModel;

    public ClientUI(HotelService hotelService, PaymentService paymentService, ReportService reportService, User user) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.user = user;
        initialiseUI();
    }

    private void initialiseUI() {
        setTitle("Hotel Management - Client: " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Search & Book", createSearchPanel());
        tabbedPane.addTab("My Reservations", createMyReservationsPanel());
        tabbedPane.addTab("Reports", createClientReportPanel());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        availableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Price"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableTable = new JTable(availableModel);
        JScrollPane scrollPane = new JScrollPane(availableTable);

        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtersPanel.add(new JLabel("Check-in (YYYY-MM-DD):"));
        checkInField = new JTextField(10);
        filtersPanel.add(checkInField);
        filtersPanel.add(new JLabel("Check-out (YYYY-MM-DD):"));
        checkOutField = new JTextField(10);
        filtersPanel.add(checkOutField);
        JButton searchButton = new JButton("Search");
        filtersPanel.add(searchButton);
        JButton bookButton = new JButton("Book Selected Room");
        filtersPanel.add(bookButton);

        panel.add(filtersPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAvailableRooms();
            }
        });
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookSelectedRoom();
            }
        });

        return panel;
    }

    private JPanel createMyReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        myResModel = new DefaultTableModel(new Object[]{"ID", "Room", "Check-In", "Check-Out", "Confirmed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myResTable = new JTable(myResModel);
        JScrollPane scrollPane = new JScrollPane(myResTable);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton cancelButton = new JButton("Cancel Reservation");
        JButton payButton = new JButton("Pay");
        JButton invoiceButton = new JButton("Invoice");
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(payButton);
        buttonsPanel.add(invoiceButton);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = myResTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int reservationId = (int) myResModel.getValueAt(selectedRow, 0);
                    int confirm = JOptionPane.showConfirmDialog(ClientUI.this, "Cancel this reservation?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            hotelService.cancelReservation(reservationId);
                            refreshMyReservations();
                            refreshAvailableRooms();
                        } catch (RemoteException ex) {
                            JOptionPane.showMessageDialog(ClientUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ClientUI.this, "Please select a reservation to cancel.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = myResTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int reservationId = (int) myResModel.getValueAt(selectedRow, 0);
                    try {
                        List<Reservation> reservations = hotelService.getAllReservations();
                        Reservation res = null;
                        for (Reservation r : reservations) {
                            if (r.getId() == reservationId) {
                                res = r;
                                break;
                            }
                        }
                        if (res != null) {
                            // Compute amount automatically based on invoice
                            com.hotel.model.Invoice invoice = paymentService.generateInvoice(res);
                            Payment payment = paymentService.processPayment(res, invoice.getTotal());
                            JOptionPane.showMessageDialog(ClientUI.this, "Payment successful. Payment ID: " + payment.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
                            refreshMyReservations();
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(ClientUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ClientUI.this, "Please select a reservation to pay.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        invoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = myResTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int reservationId = (int) myResModel.getValueAt(selectedRow, 0);
                    try {
                        List<Reservation> reservations = hotelService.getAllReservations();
                        Reservation res = null;
                        for (Reservation r : reservations) {
                            if (r.getId() == reservationId) {
                                res = r;
                                break;
                            }
                        }
                        if (res != null) {
                            com.hotel.model.Invoice invoice = paymentService.generateInvoice(res);
                            JOptionPane.showMessageDialog(ClientUI.this, String.format("Invoice:\nReservation ID: %d\nTotal: %.2f", res.getId(), invoice.getTotal()), "Invoice", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(ClientUI.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(ClientUI.this, "Please select a reservation to invoice.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel createClientReportPanel() {
        // Provide a simple client view of occupancy and revenue for interest
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(15, 50);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(area);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton myHistoryButton = new JButton("My Reservation History");
        buttons.add(myHistoryButton);
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        myHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                area.setText(reportService.generateClientHistoryReport(user.getClient()));
            }
        });
        return panel;
    }

    private void refreshAvailableRooms() {
        availableModel.setRowCount(0);
        try {
            List<Room> rooms = hotelService.getAvailableRooms();
            for (Room r : rooms) {
                availableModel.addRow(new Object[]{r.getId(), r.getType(), r.getPrice()});
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bookSelectedRoom() {
        int selectedRow = availableTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int roomId = (int) availableModel.getValueAt(selectedRow, 0);
                // find room by id
                List<Room> rooms = hotelService.getAvailableRooms();
                Room selectedRoom = null;
                for (Room r : rooms) {
                    if (r.getId() == roomId) {
                        selectedRoom = r;
                        break;
                    }
                }
                if (selectedRoom == null) {
                    JOptionPane.showMessageDialog(this, "Selected room is no longer available.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    refreshAvailableRooms();
                    return;
                }
                // parse dates
                String checkInText = checkInField.getText().trim();
                String checkOutText = checkOutField.getText().trim();
                LocalDate checkIn;
                LocalDate checkOut;
                try {
                    checkIn = LocalDate.parse(checkInText);
                    checkOut = LocalDate.parse(checkOutText);
                    if (checkOut.isBefore(checkIn) || checkOut.equals(checkIn)) {
                        JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid dates in YYYY-MM-DD format.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Reservation res = hotelService.makeReservation(user.getClient(), selectedRoom, checkIn, checkOut);
                if (res != null) {
                    JOptionPane.showMessageDialog(this, "Reservation successful (ID: " + res.getId() + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshAvailableRooms();
                    refreshMyReservations();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not create reservation.", "Failure", JOptionPane.WARNING_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a room to book.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshMyReservations() {
        myResModel.setRowCount(0);
        try {
            List<Reservation> reservations = hotelService.getAllReservations();
            for (Reservation r : reservations) {
                if (r.getClient() != null && r.getClient().getId() == user.getClient().getId()) {
                    myResModel.addRow(new Object[]{
                            r.getId(),
                            r.getRoom() != null ? r.getRoom().getId() : "",
                            r.getCheckInDate(),
                            r.getCheckOutDate(),
                            r.isConfirmed() ? "Yes" : "No"
                    });
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
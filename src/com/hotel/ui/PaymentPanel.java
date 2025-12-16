package com.hotel.ui;

import com.hotel.db.PaymentDao;
import com.hotel.db.PaymentDaoImpl;
import com.hotel.model.Client;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.rmi.RemoteException;
import java.util.List;

public class PaymentPanel extends JPanel {

    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final PaymentDao paymentDao = new PaymentDaoImpl();

    private JTable table;
    private DefaultTableModel tableModel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public PaymentPanel(HotelService hotelService, PaymentService paymentService) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        initialiseUI();
        refreshTable();
    }

    private void initialiseUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Client", "Reservation", "Amount", "Date", "Paid"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton processButton = new JButton("Process Payment");
        JButton invoiceButton = new JButton("Generate Invoice");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(processButton);
        buttonPanel.add(invoiceButton);
        buttonPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // -------- Actions --------

        processButton.addActionListener(e -> showProcessPaymentDialog());

        invoiceButton.addActionListener(e -> showGenerateInvoiceDialog());

        refreshButton.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Payment> payments = paymentDao.findAll();

        for (Payment p : payments) {
            String clientLabel = "";
            if (p.getClient() != null) {
                clientLabel = p.getClient().getName();
                if (p.getClient().getEmail() != null) {
                    clientLabel += " (" + p.getClient().getEmail() + ")";
                }
            }

            String reservationLabel = (p.getReservation() != null)
                    ? p.getReservation().toString()
                    : "";

            String dateLabel = (p.getDate() != null)
                    ? p.getDate().format(dateFormatter)
                    : "";

            tableModel.addRow(new Object[]{
                    p.getId(),
                    clientLabel,
                    reservationLabel,
                    p.getAmount(),
                    dateLabel,
                    p.isPaid()
            });
        }
    }


    private void showProcessPaymentDialog() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Process Payment",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel reservationLabel = new JLabel("Reservation:");
        JComboBox<Reservation> reservationCombo = new JComboBox<>();

        try {
            List<Reservation> reservations = hotelService.getAllReservations();
            for (Reservation r : reservations) {
                reservationCombo.addItem(r);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(10);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(reservationLabel, gbc);
        gbc.gridx = 1;
        panel.add(reservationCombo, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        row++;

        JButton processButton = new JButton("Process");
        JButton cancelButton = new JButton("Cancel");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(processButton);
        btnPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnPanel, gbc);

        processButton.addActionListener(e -> {
            Reservation reservation = (Reservation) reservationCombo.getSelectedItem();
            if (reservation == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a reservation.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please enter an amount.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid amount.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                paymentService.processPayment(reservation, amount);  // 🔥 persiste via DAO
                refreshTable();
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error processing payment: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void showGenerateInvoiceDialog() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Generate Invoice",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel reservationLabel = new JLabel("Reservation:");
        JComboBox<Reservation> reservationCombo = new JComboBox<>();

        try {
            List<Reservation> reservations = hotelService.getAllReservations();
            for (Reservation r : reservations) {
                reservationCombo.addItem(r);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(reservationLabel, gbc);
        gbc.gridx = 1;
        panel.add(reservationCombo, gbc);
        row++;

        JButton generateButton = new JButton("Generate");
        JButton cancelButton = new JButton("Cancel");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(generateButton);
        btnPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(btnPanel, gbc);

        generateButton.addActionListener(e -> {
            Reservation reservation = (Reservation) reservationCombo.getSelectedItem();
            if (reservation == null) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a reservation.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                var invoice = paymentService.generateInvoice(reservation);
                JOptionPane.showMessageDialog(dialog,
                        String.format("Invoice generated.\nTotal: %.2f", invoice.getTotalAmount()),
                        "Invoice",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error generating invoice: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}

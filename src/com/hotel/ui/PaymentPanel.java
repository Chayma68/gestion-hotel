package com.hotel.ui;

import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel that allows employees to process payments and generate
 * invoices.  All payments are displayed in a table.  Processing a
 * payment involves selecting a reservation and entering the amount
 * paid.  Generating an invoice simply displays the computed total
 * based on the room price and length of stay.
 */
public class PaymentPanel extends JPanel {
    private final HotelService hotelService;
    private final PaymentService paymentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public PaymentPanel(HotelService hotelService, PaymentService paymentService) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        initialiseUI();
        refreshTable();
    }

    private void initialiseUI() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Client", "Reservation", "Amount", "Date", "Paid"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton payButton = new JButton("Process Payment");
        JButton invoiceButton = new JButton("Generate Invoice");
        JButton refreshButton = new JButton("Refresh");
        buttonsPanel.add(payButton);
        buttonsPanel.add(invoiceButton);
        buttonsPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPaymentDialog();
            }
        });

        invoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showInvoiceDialog();
            }
        });

        refreshButton.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Payment> payments = paymentService.getPaymentsForClient(-1); // we will override below
        // PaymentService does not support getAll; but we can get total by calling getPaymentsForClient for each client.
        // For simplicity we will accumulate from internal payments via reflection by calling a protected method.  Instead we
        // rely on the PaymentPanel storing a reference to PaymentServiceImpl and retrieving all payments.  Since the
        // interface does not provide a method for retrieving all payments, we cast if necessary.
        try {
            if (paymentService instanceof com.hotel.service.ejb.PaymentServiceImpl) {
                com.hotel.service.ejb.PaymentServiceImpl impl = (com.hotel.service.ejb.PaymentServiceImpl) paymentService;
                java.lang.reflect.Field field = impl.getClass().getDeclaredField("payments");
                field.setAccessible(true);
                payments = (List<Payment>) field.get(impl);
            }
        } catch (Exception e) {
            // ignore reflection issues
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Payment p : payments) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getClient() != null ? p.getClient().getName() : "",
                    p.getReservation() != null ? p.getReservation().getId() : "",
                    p.getAmount(),
                    p.getDate() != null ? p.getDate().format(formatter) : "",
                    p.isPaid() ? "Yes" : "No"
            });
        }
    }

    private void showPaymentDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Process Payment", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            List<Reservation> reservations = hotelService.getAllReservations();
            if (reservations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No reservations available.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JLabel reservationLabel = new JLabel("Reservation:");
            JComboBox<Reservation> reservationCombo = new JComboBox<>(reservations.toArray(new Reservation[0]));
            JLabel amountLabel = new JLabel("Amount:");
            JTextField amountField = new JTextField(20);

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(reservationLabel, gbc);
            gbc.gridx = 1;
            panel.add(reservationCombo, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(amountLabel, gbc);
            gbc.gridx = 1;
            panel.add(amountField, gbc);

            JButton okButton = new JButton("Process");
            JButton cancelButton = new JButton("Cancel");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Reservation res = (Reservation) reservationCombo.getSelectedItem();
                    String amountText = amountField.getText().trim();
                    double amount;
                    try {
                        amount = Double.parseDouble(amountText);
                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(dialog, "Amount must be positive.", "Validation", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid amount.", "Validation", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Payment payment = paymentService.processPayment(res, amount);
                    JOptionPane.showMessageDialog(dialog, "Payment processed successfully (ID: " + payment.getId() + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                    dialog.dispose();
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

    private void showInvoiceDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Generate Invoice", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            List<Reservation> reservations = hotelService.getAllReservations();
            if (reservations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No reservations available.", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JLabel reservationLabel = new JLabel("Reservation:");
            JComboBox<Reservation> reservationCombo = new JComboBox<>(reservations.toArray(new Reservation[0]));

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(reservationLabel, gbc);
            gbc.gridx = 1;
            panel.add(reservationCombo, gbc);

            JButton okButton = new JButton("Generate");
            JButton cancelButton = new JButton("Cancel");
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            panel.add(buttonPanel, gbc);

            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Reservation res = (Reservation) reservationCombo.getSelectedItem();
                    com.hotel.model.Invoice invoice = paymentService.generateInvoice(res);
                    JOptionPane.showMessageDialog(dialog, String.format("Invoice generated:\nReservation ID: %d\nClient: %s\nTotal: %.2f", res.getId(), res.getClient().getName(), invoice.getTotal()), "Invoice", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
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
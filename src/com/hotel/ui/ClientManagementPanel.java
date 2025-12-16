package com.hotel.ui;

import com.hotel.model.Client;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;


public class ClientManagementPanel extends JPanel {
    private final HotelService hotelService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ClientManagementPanel(HotelService hotelService) {
        this.hotelService = hotelService;
        initialiseUI();
        refreshTable();
    }

    private void initialiseUI() {
        setLayout(new BorderLayout());
        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Contact", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Client");
        JButton searchButton = new JButton("Search Client");
        JButton refreshButton = new JButton("Refresh");
        buttonsPanel.add(addButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showClientDialog();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(ClientManagementPanel.this, "Enter client name:", "Search Client", JOptionPane.QUESTION_MESSAGE);
                if (name != null && !name.trim().isEmpty()) {
                    try {
                        Client client = hotelService.findClientByName(name.trim());
                        if (client != null) {
                            JOptionPane.showMessageDialog(ClientManagementPanel.this, client.toString(), "Client Found", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(ClientManagementPanel.this, "Client not found.", "Search", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(ClientManagementPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        try {
            List<Client> clients = hotelService.getAllClients();
            tableModel.setRowCount(0);
            for (Client c : clients) {
                tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getContact(), c.getEmail()});
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showClientDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Add Client", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        JLabel contactLabel = new JLabel("Contact:");
        JTextField contactField = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(contactLabel, gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        JButton okButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();
                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and contact are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    Client client = new Client(0, name, contact, email);
                    hotelService.addClient(client);
                    refreshTable();
                    dialog.dispose();
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
    }
}
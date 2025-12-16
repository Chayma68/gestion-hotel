package com.hotel.ui;

import com.hotel.model.Room;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private final HotelService hotelService;
    private JTable table;
    private DefaultTableModel tableModel;

    public RoomManagementPanel(HotelService hotelService) {
        this.hotelService = hotelService;
        initialiseUI();
        refreshTable();
    }

    private void initialiseUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Number", "Type", "Price", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Room");
        JButton editButton = new JButton("Edit Room");
        JButton deleteButton = new JButton("Delete Room");
        JButton refreshButton = new JButton("Refresh");
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> showRoomDialog(null));

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int roomId = (int) tableModel.getValueAt(selectedRow, 0);
                    List<Room> rooms = hotelService.getAllRooms();
                    for (Room r : rooms) {
                        if (r.getId() == roomId) {
                            showRoomDialog(r);
                            break;
                        }
                    }
                } catch (RemoteException ex) {
                    JOptionPane.showMessageDialog(RoomManagementPanel.this,
                            "Error: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(RoomManagementPanel.this,
                        "Please select a room to edit.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(RoomManagementPanel.this,
                        "Are you sure you want to delete this room?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
                        hotelService.deleteRoom(roomId);
                        refreshTable();
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(RoomManagementPanel.this,
                                "Error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(RoomManagementPanel.this,
                        "Please select a room to delete.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        try {
            List<Room> rooms = hotelService.getAllRooms();
            tableModel.setRowCount(0);
            for (Room r : rooms) {
                tableModel.addRow(new Object[]{
                        r.getId(),
                        r.getNumber(),
                        r.getType(),
                        r.getPrice(),
                        r.isAvailable()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRoomDialog(Room room) {
        boolean editing = room != null;

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                editing ? "Edit Room" : "Add Room",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel numberLabel = new JLabel("Number:");
        JTextField numberField = new JTextField(10);

        JLabel typeLabel = new JLabel("Type:");
        JTextField typeField = new JTextField(20);

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(20);

        JLabel availableLabel = new JLabel("Available:");
        JCheckBox availableCheckBox = new JCheckBox();

        int row = 0;

        // Number
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(numberLabel, gbc);
        gbc.gridx = 1;
        panel.add(numberField, gbc);
        row++;

        // Type
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(typeLabel, gbc);
        gbc.gridx = 1;
        panel.add(typeField, gbc);
        row++;

        // Price
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(priceLabel, gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);
        row++;

        // Available
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(availableLabel, gbc);
        gbc.gridx = 1;
        panel.add(availableCheckBox, gbc);
        row++;

        // Pre-fill when editing
        if (editing) {
            numberField.setText(room.getNumber());
            typeField.setText(room.getType());
            priceField.setText(String.valueOf(room.getPrice()));
            availableCheckBox.setSelected(room.isAvailable());
        } else {
            availableCheckBox.setSelected(true);
        }

        JButton okButton = new JButton(editing ? "Update" : "Add");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        okButton.addActionListener((ActionEvent e) -> {
            String number = numberField.getText().trim();
            String type = typeField.getText().trim();
            String priceText = priceField.getText().trim();
            boolean available = availableCheckBox.isSelected();

            if (number.isEmpty() || type.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill all fields.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid price value.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (editing) {
                    room.setNumber(number);
                    room.setType(type);
                    room.setPrice(price);
                    room.setAvailable(available);
                    hotelService.updateRoom(room);
                } else {
                    Room newRoom = new Room();
                    newRoom.setNumber(number);
                    newRoom.setType(type);
                    newRoom.setPrice(price);
                    newRoom.setAvailable(available);
                    hotelService.addRoom(newRoom);
                }
                refreshTable();
                dialog.dispose();
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
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

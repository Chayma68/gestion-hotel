package com.hotel.ui;

import com.hotel.model.Room;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Panel allowing employees to manage hotel rooms.  Users can add new
 * rooms, edit existing room details (type and price) and delete
 * rooms.  A JTable displays all rooms and reflects changes in
 * real time after each operation.
 */
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

        // Table model and table
        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Price", "Available"}, 0) {
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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRoomDialog(null);
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    try {
                        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
                        // Find the room from the service
                        List<Room> rooms = hotelService.getAllRooms();
                        for (Room r : rooms) {
                            if (r.getId() == roomId) {
                                showRoomDialog(r);
                                break;
                            }
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(RoomManagementPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(RoomManagementPanel.this, "Please select a room to edit.", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int confirm = JOptionPane.showConfirmDialog(RoomManagementPanel.this, "Are you sure you want to delete this room?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            int roomId = (int) tableModel.getValueAt(selectedRow, 0);
                            hotelService.deleteRoom(roomId);
                            refreshTable();
                        } catch (RemoteException ex) {
                            JOptionPane.showMessageDialog(RoomManagementPanel.this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(RoomManagementPanel.this, "Please select a room to delete.", "Information", JOptionPane.INFORMATION_MESSAGE);
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

    /**
     * Populates the table with the current list of rooms from the
     * service.
     */
    private void refreshTable() {
        try {
            List<Room> rooms = hotelService.getAllRooms();
            tableModel.setRowCount(0);
            for (Room r : rooms) {
                tableModel.addRow(new Object[]{r.getId(), r.getType(), r.getPrice(), r.isAvailable()});
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays a modal dialog to add or edit a room.  When editing the
     * provided room parameter must not be null.  When the user
     * confirms the dialog the service is invoked accordingly and the
     * table is refreshed.
     *
     * @param room room to edit or null to add a new room
     */
    private void showRoomDialog(Room room) {
        boolean editing = room != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), editing ? "Edit Room" : "Add Room", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel typeLabel = new JLabel("Type:");
        JTextField typeField = new JTextField(20);
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(20);

        // Pre-fill when editing
        if (editing) {
            typeField.setText(room.getType());
            priceField.setText(String.valueOf(room.getPrice()));
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(typeLabel, gbc);
        gbc.gridx = 1;
        panel.add(typeField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(priceLabel, gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        JButton okButton = new JButton(editing ? "Update" : "Add");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = typeField.getText().trim();
                String priceText = priceField.getText().trim();
                if (type.isEmpty() || priceText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double price;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid price value.", "Validation", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    if (editing) {
                        room.setType(type);
                        room.setPrice(price);
                        hotelService.updateRoom(room);
                    } else {
                        Room newRoom = new Room(0, type, price, true);
                        hotelService.addRoom(newRoom);
                    }
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
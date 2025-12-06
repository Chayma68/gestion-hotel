package com.hotel.ui;

import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Simple login window that prompts the user for a username and
 * password.  Successful authentication opens either the employee or
 * client interface depending on the user’s role.  The frame
 * references the hotel services and payment services which are
 * injected via the constructor.
 */
public class LoginFrame extends JFrame {
    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final List<User> users;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame(HotelService hotelService, PaymentService paymentService, ReportService reportService, List<User> users) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.users = users;
        initialiseUI();
    }

    /**
     * Configures the swing components and layout for the login form.
     */
    private void initialiseUI() {
        setTitle("Hotel Management - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(loginButton, gbc);

        messageLabel = new JLabel();
        messageLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(messageLabel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        getContentPane().add(panel);
    }

    /**
     * Validates the entered username and password against the list of
     * known users.  On success opens the appropriate main window and
     * disposes of the login frame.  On failure displays an error.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                // Successful login
                SwingUtilities.invokeLater(() -> {
                    if (user.getRole() == User.Role.EMPLOYEE) {
                        EmployeeUI ui = new EmployeeUI(hotelService, paymentService, reportService, user);
                        ui.setVisible(true);
                    } else {
                        ClientUI ui = new ClientUI(hotelService, paymentService, reportService, user);
                        ui.setVisible(true);
                    }
                    dispose();
                });
                return;
            }
        }
        messageLabel.setText("Invalid credentials. Please try again.");
    }
}
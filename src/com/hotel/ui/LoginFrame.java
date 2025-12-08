package com.hotel.ui;

import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class LoginFrame extends JFrame {

    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final List<User> users;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame(HotelService hotelService,
                      PaymentService paymentService,
                      ReportService reportService,
                      List<User> users) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.users = users;

        initUI();
    }

    private void initUI() {
        setTitle("Gestion d'hôtel - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);  // centre la fenêtre
        setResizable(false);

        // ----- Panel principal -----
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ----- En-tête -----
        JLabel titleLabel = new JLabel("Système de gestion d'hôtel", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ----- Formulaire -----
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Nom d'utilisateur :");
        JLabel passLabel = new JLabel("Mot de passe :");

        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ----- Zone bas : message + boutons -----
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        bottomPanel.add(messageLabel, BorderLayout.NORTH);

        JButton loginButton = new JButton("Se connecter");
        JButton exitButton = new JButton("Quitter");

        loginButton.addActionListener(this::onLogin);
        exitButton.addActionListener(e -> System.exit(0));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(loginButton);
        buttonsPanel.add(exitButton);

        bottomPanel.add(buttonsPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void onLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        User found = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username)
                        && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (found == null) {
            messageLabel.setText("Identifiants incorrects.");
            return;
        }

        // Connexion réussie
        SwingUtilities.invokeLater(() -> {
            if (found.getRole() == User.Role.EMPLOYEE) {
                new EmployeeUI(hotelService, paymentService, reportService, found).setVisible(true);
            } else {
                new ClientUI(hotelService, paymentService, reportService, found).setVisible(true);
            }
        });

        dispose();
    }
}

package com.hotel.ui;

import com.hotel.db.UserDao;
import com.hotel.model.Client;
import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class LoginFrame extends JFrame {

    private final HotelService hotelService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final UserDao userDao;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame(HotelService hotelService,
                      PaymentService paymentService,
                      ReportService reportService,
                      UserDao userDao) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.userDao = userDao;

        setTitle("Gestion d'hôtel - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 320);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(230, 233, 238));

        JLabel titleLabel = new JLabel("Système de gestion d'hôtel", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Nom d'utilisateur : ");
        JLabel passwordLabel = new JLabel("Mot de passe : ");

        usernameField = new JTextField(22);
        passwordField = new JPasswordField(22);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Se connecter");
        JButton quitButton = new JButton("Quitter");
        JButton registerButton = new JButton("Créer un compte client");

        buttonPanel.add(loginButton);
        buttonPanel.add(quitButton);
        buttonPanel.add(registerButton);

        loginButton.addActionListener(e -> handleLogin());
        quitButton.addActionListener(e -> System.exit(0));
        registerButton.addActionListener(e -> showClientRegistrationDialog());

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(messageLabel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        //  Auth via BD
        User matched = userDao.findByUsernameAndPassword(username, password);

        if (matched == null) {
            messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            return;
        }

        messageLabel.setText(" ");

        if (matched.getRole() == User.Role.EMPLOYEE) {
            SwingUtilities.invokeLater(() -> {
                EmployeeUI ui = new EmployeeUI(hotelService, paymentService, reportService, matched);
                ui.setVisible(true);
            });
        } else if (matched.getRole() == User.Role.CLIENT) {
            SwingUtilities.invokeLater(() -> {
                ClientUI ui = new ClientUI(hotelService, paymentService, reportService, matched);
                ui.setVisible(true);
            });
        }

        dispose();
    }

    private void showClientRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Créer un compte client", true);
        dialog.setSize(470, 340);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Nom complet :");
        JLabel contactLabel = new JLabel("Contact / Téléphone :");
        JLabel emailLabel = new JLabel("Email :");
        JLabel usernameLabel = new JLabel("Nom d'utilisateur :");
        JLabel passwordLabel = new JLabel("Mot de passe :");

        JTextField nameField = new JTextField(22);
        JTextField contactField = new JTextField(22);
        JTextField emailField = new JTextField(22);
        JTextField usernameField = new JTextField(22);
        JPasswordField passwordField = new JPasswordField(22);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(contactLabel, gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        row++;

        JButton createButton = new JButton("Créer le compte");
        JButton cancelButton = new JButton("Annuler");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        createButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Veuillez remplir au moins nom, email, identifiant et mot de passe.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // vérifier en BD
            if (userDao.findByUsername(username) != null) {
                JOptionPane.showMessageDialog(dialog,
                        "Ce nom d'utilisateur est déjà utilisé.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // 1) créer client (BD)
                Client client = new Client();
                client.setName(name);
                client.setContact(contact);
                client.setEmail(email);

                hotelService.addClient(client); // doit remplir client.id

                // 2) créer user (BD)
                User newUser = new User(0, username, password, User.Role.CLIENT, client);
                userDao.save(newUser);

                JOptionPane.showMessageDialog(dialog,
                        "Compte créé avec succès.\nVous pouvez maintenant vous connecter.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (RemoteException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Erreur lors de la création : " + ex.getMessage(),
                        "Erreur",
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

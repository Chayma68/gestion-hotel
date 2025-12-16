package com.hotel;

import com.hotel.db.UserDao;
import com.hotel.db.UserDaoImpl;
import com.hotel.model.User;
import com.hotel.service.ReportService;
import com.hotel.service.ReportServiceImpl;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.ejb.PaymentServiceImpl;
import com.hotel.service.rmi.HotelService;
import com.hotel.service.rmi.HotelServiceImpl;
import com.hotel.ui.LoginFrame;

import javax.swing.*;
import java.rmi.RemoteException;

public class HotelManagementApp {

    public static void main(String[] args) {

        // Look & Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) { }

        try {
            // Services
            HotelService hotelService = new HotelServiceImpl();
            PaymentService paymentService = new PaymentServiceImpl();
            ReportService reportService = new ReportServiceImpl(hotelService, paymentService);

            // DAO Users
            UserDao userDao = new UserDaoImpl();

            // Seed admin si absent
            if (userDao.findByUsername("admin") == null) {
                userDao.save(new User(0, "admin", "admin", User.Role.EMPLOYEE));
            }

            // UI
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(hotelService, paymentService, reportService, userDao);
                loginFrame.setVisible(true);
            });

        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Erreur au démarrage (RMI/Service): " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

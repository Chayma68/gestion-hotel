package com.hotel.service.ejb;

import com.hotel.db.PaymentDao;
import com.hotel.db.PaymentDaoImpl;
import com.hotel.model.Invoice;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    // ✅ DAO pour les paiements (MySQL)
    private final PaymentDao paymentDao = new PaymentDaoImpl();

    // ✅ On garde les factures en mémoire (tu pourras les passer en DAO plus tard)
    private final List<Invoice> invoices = new ArrayList<>();
    private int nextInvoiceId = 1;

    public PaymentServiceImpl() {
    }

    @Override
    public Payment processPayment(Reservation reservation, double amount) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        // On crée l'objet Payment (sans id, il sera généré par la BD)
        Payment payment = new Payment();
        payment.setClient(reservation.getClient());
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        payment.setPaid(true);

        // ✅ Persistance via DAO
        paymentDao.save(payment);

        return payment;
    }

    @Override
    public Invoice generateInvoice(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        long days = ChronoUnit.DAYS.between(
                reservation.getCheckInDate(),
                reservation.getCheckOutDate()
        );
        if (days <= 0) {
            days = 1;
        }
        double total = days * reservation.getRoom().getPrice();

        Invoice invoice = new Invoice(nextInvoiceId++, reservation, LocalDate.now(), total);
        invoices.add(invoice);
        return invoice;
    }

    @Override
    public List<Payment> getPaymentsForClient(int clientId) {
        // ✅ On délègue au DAO (les objets Payment sont reconstruits à partir de la BD)
        return paymentDao.findByClientId(clientId);
    }

    @Override
    public double getTotalRevenue() {
        // ✅ Somme des montants calculée côté BD
        return paymentDao.getTotalRevenue();
    }
}

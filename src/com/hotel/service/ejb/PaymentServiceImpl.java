package com.hotel.service.ejb;

import com.hotel.db.InvoiceDao;
import com.hotel.db.InvoiceDaoImpl;
import com.hotel.db.PaymentDao;
import com.hotel.db.PaymentDaoImpl;
import com.hotel.model.Invoice;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao = new PaymentDaoImpl();
    private final InvoiceDao invoiceDao = new InvoiceDaoImpl();

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

        Payment payment = new Payment();
        payment.setClient(reservation.getClient());
        payment.setReservation(reservation);
        payment.setAmount(amount);
        payment.setDate(LocalDate.now());
        payment.setPaid(true);

        return paymentDao.save(payment);
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

        Invoice invoice = new Invoice();
        invoice.setReservation(reservation);
        invoice.setDate(LocalDate.now());
        invoice.setTotalAmount(total);

        //  Persistance BD
        return invoiceDao.save(invoice);
    }

    @Override
    public List<Payment> getPaymentsForClient(int clientId) {
        return paymentDao.findByClientId(clientId);
    }

    @Override
    public double getTotalRevenue() {
        return paymentDao.getTotalRevenue();
    }
}

package com.hotel.service.ejb;

import com.hotel.model.Client;
import com.hotel.model.Invoice;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple implementation of {@link PaymentService} that stores
 * payments in memory.  In a real EJB deployment this class would be
 * annotated with {@code @Stateless} and rely on container managed
 * services for persistence, transactions and security.  Here it
 * functions as a plain Java class to keep the example self contained.
 */
public class PaymentServiceImpl implements PaymentService {
    private final List<Payment> payments;
    private final List<Invoice> invoices;
    private int nextPaymentId = 1;
    private int nextInvoiceId = 1;

    public PaymentServiceImpl() {
        this.payments = new ArrayList<>();
        this.invoices = new ArrayList<>();
    }

    @Override
    public Payment processPayment(Reservation reservation, double amount) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        Payment payment = new Payment(nextPaymentId++, reservation.getClient(), reservation, amount, LocalDate.now(), true);
        payments.add(payment);
        return payment;
    }

    @Override
    public Invoice generateInvoice(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        long days = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
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
        return payments.stream()
                .filter(p -> p.getClient() != null && p.getClient().getId() == clientId)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalRevenue() {
        return payments.stream().mapToDouble(Payment::getAmount).sum();
    }
}
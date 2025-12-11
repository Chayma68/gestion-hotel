package com.hotel.service.ejb;

import com.hotel.model.Invoice;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;

import java.util.List;


public interface PaymentService {

    Payment processPayment(Reservation reservation, double amount);

    Invoice generateInvoice(Reservation reservation);

    List<Payment> getPaymentsForClient(int clientId);

    double getTotalRevenue();
}
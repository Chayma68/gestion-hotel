package com.hotel.service.ejb;

import com.hotel.model.Invoice;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;

import java.util.List;

/**
 * Defines the contract for payment and billing services.  In a
 * traditional enterprise application this interface would be
 * implemented by an EJB component to benefit from container managed
 * transactions and security.  It is kept simple here to avoid
 * external dependencies.
 */
public interface PaymentService {
    /**
     * Processes payment for a given reservation.  The amount must be
     * provided explicitly and is recorded on the returned Payment
     * object.  In a real system additional checks would be made
     * regarding payment method and validation.
     *
     * @param reservation reservation being paid
     * @param amount      amount paid
     * @return Payment instance
     */
    Payment processPayment(Reservation reservation, double amount);

    /**
     * Generates an invoice for the provided reservation.  The total
     * amount due is derived from the room price and the length of stay.
     *
     * @param reservation reservation to invoice
     * @return Invoice instance
     */
    Invoice generateInvoice(Reservation reservation);

    /**
     * Returns all payments associated with a particular client.
     *
     * @param clientId identifier of client
     * @return list of payments
     */
    List<Payment> getPaymentsForClient(int clientId);

    /**
     * Calculates the total revenue the hotel has received from all
     * payments.
     *
     * @return total revenue
     */
    double getTotalRevenue();
}
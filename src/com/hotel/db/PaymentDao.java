package com.hotel.db;

import com.hotel.model.Payment;

import java.util.List;

public interface PaymentDao {


    Payment save(Payment payment);
    List<Payment> findByClientId(int clientId);
    List<Payment> findAll();
    double getTotalRevenue();
}

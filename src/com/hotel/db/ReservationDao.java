package com.hotel.db;

import com.hotel.model.Reservation;

import java.util.List;

public interface ReservationDao {

    Reservation save(Reservation reservation);

    List<Reservation> findAll();

    Reservation findById(int id);

    void delete(int id);

    void update(Reservation reservation);
}

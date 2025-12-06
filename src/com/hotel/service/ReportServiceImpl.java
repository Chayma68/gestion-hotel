package com.hotel.service;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Implementation of {@link ReportService} that composes a
 * {@link HotelService} and a {@link PaymentService} to derive
 * summarised statistics.  Exceptions thrown by the underlying
 * {@code HotelService} are wrapped into RuntimeExceptions since
 * report generation occurs locally within the UI layer in this
 * example.
 */
public class ReportServiceImpl implements ReportService {
    private final HotelService hotelService;
    private final PaymentService paymentService;

    public ReportServiceImpl(HotelService hotelService, PaymentService paymentService) {
        this.hotelService = hotelService;
        this.paymentService = paymentService;
    }

    @Override
    public String generateOccupancyReport() {
        try {
            List<Room> rooms = hotelService.getAllRooms();
            List<Room> available = hotelService.getAvailableRooms();
            int total = rooms.size();
            int free = available.size();
            int booked = total - free;
            double occupancyRate = total == 0 ? 0.0 : (double) booked / total * 100.0;
            return String.format("Occupancy Report:\nTotal rooms: %d\nRooms occupied: %d\nRooms available: %d\nOccupancy rate: %.2f%%", total, booked, free, occupancyRate);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateRevenueReport() {
        double revenue = paymentService.getTotalRevenue();
        return String.format("Revenue Report:\nTotal revenue: %.2f", revenue);
    }

    @Override
    public String generateClientHistoryReport(Client client) {
        if (client == null) {
            return "No client selected.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("History for client: ").append(client.getName()).append("\n");
        List<Reservation> reservations;
        try {
            reservations = hotelService.getAllReservations();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        reservations.stream()
                .filter(r -> r.getClient() != null && r.getClient().getId() == client.getId())
                .forEach(res -> sb.append(String.format("Reservation %d - Room %d (%s to %s) - Confirmed: %s\n",
                        res.getId(),
                        res.getRoom() != null ? res.getRoom().getId() : 0,
                        res.getCheckInDate(),
                        res.getCheckOutDate(),
                        res.isConfirmed() ? "yes" : "no")));
        return sb.toString();
    }
}
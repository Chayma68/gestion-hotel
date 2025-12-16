package com.hotel.service;

import com.hotel.db.PaymentDao;
import com.hotel.db.PaymentDaoImpl;
import com.hotel.db.ReservationDao;
import com.hotel.db.ReservationDaoImpl;
import com.hotel.db.RoomDao;
import com.hotel.db.RoomDaoImpl;
import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.service.ejb.PaymentService;
import com.hotel.service.rmi.HotelService;

import java.util.List;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {


    private final RoomDao roomDao;
    private final PaymentDao paymentDao;
    private final ReservationDao reservationDao;


    public ReportServiceImpl(HotelService hotelService, PaymentService paymentService) {
        this.roomDao = new RoomDaoImpl();
        this.paymentDao = new PaymentDaoImpl();
        this.reservationDao = new ReservationDaoImpl();
    }

    @Override
    public String generateOccupancyReport() {

        List<Room> rooms = roomDao.getAllRooms();
        List<Room> available = roomDao.getAvailableRooms();

        int total = rooms.size();
        int free = available.size();
        int booked = total - free;
        double occupancyRate = total == 0 ? 0.0 : (double) booked / total * 100.0;

        return String.format(
                "Occupancy Report:\n" +
                        "Total rooms: %d\n" +
                        "Rooms occupied: %d\n" +
                        "Rooms available: %d\n" +
                        "Occupancy rate: %.2f%%",
                total, booked, free, occupancyRate
        );
    }

    @Override
    public String generateRevenueReport() {

        double revenue = paymentDao.getTotalRevenue();
        return String.format("Revenue Report:\nTotal revenue: %.2f", revenue);
    }

    @Override
    public String generateClientHistoryReport(Client client) {
        if (client == null) {
            return "No client selected.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("History for client: ")
                .append(client.getName())
                .append(" (id=")
                .append(client.getId())
                .append(")\n");

        List<Reservation> reservations = reservationDao.findAll();

        List<Reservation> clientReservations = reservations.stream()
                .filter(r -> r.getClient() != null && r.getClient().getId() == client.getId())
                .collect(Collectors.toList());

        if (clientReservations.isEmpty()) {
            sb.append("No reservations found.\n");
        } else {
            for (Reservation res : clientReservations) {
                sb.append(String.format(
                        "Reservation %d - Room %s (%s → %s) - Confirmed: %s\n",
                        res.getId(),
                        res.getRoom() != null
                                ? (res.getRoom().getNumber() != null
                                ? res.getRoom().getNumber()
                                : String.valueOf(res.getRoom().getId()))
                                : "?",
                        res.getCheckInDate(),
                        res.getCheckOutDate(),
                        res.isConfirmed() ? "yes" : "no"
                ));
            }
        }

        return sb.toString();
    }
}

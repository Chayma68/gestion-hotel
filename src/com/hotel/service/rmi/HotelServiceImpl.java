package com.hotel.service.rmi;

import com.hotel.db.ClientDao;
import com.hotel.db.ClientDaoImpl;
import com.hotel.db.ReservationDao;
import com.hotel.db.ReservationDaoImpl;
import com.hotel.db.RoomDao;
import com.hotel.db.RoomDaoImpl;
import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.List;

public class HotelServiceImpl extends UnicastRemoteObject implements HotelService {
    private static final long serialVersionUID = 1L;

    private final ClientDao clientDao = new ClientDaoImpl();
    private final RoomDao roomDao = new RoomDaoImpl();
    private final ReservationDao reservationDao = new ReservationDaoImpl();

    public HotelServiceImpl() throws RemoteException {
        super();
        initialiseSampleData();
    }

    private void initialiseSampleData() {
        // Chambres
        if (roomDao.getAllRooms().isEmpty()) {
            roomDao.addRoom(new Room(0, "101", "Single", 50.0, true));
            roomDao.addRoom(new Room(0, "102", "Double", 80.0, true));
            roomDao.addRoom(new Room(0, "201", "Suite", 120.0, true));
        }

        // Clients
        if (clientDao.findAll().isEmpty()) {
            clientDao.add(new Client(0, "Alice", "1234 Avenue Street", "alice@example.com"));
            clientDao.add(new Client(0, "Bob", "5678 Boulevard", "bob@example.com"));
        }
    }

    // -------------------- Room operations --------------------

    @Override
    public synchronized List<Room> getAllRooms() {
        return roomDao.getAllRooms();
    }

    @Override
    public synchronized List<Room> getAvailableRooms() {
        return roomDao.getAvailableRooms();
    }

    @Override
    public synchronized void addRoom(Room room) {
        roomDao.addRoom(room);
    }

    @Override
    public synchronized void updateRoom(Room room) {
        roomDao.updateRoom(room);
    }

    @Override
    public synchronized void deleteRoom(int id) {
        roomDao.deleteRoom(id);
    }

    // -------------------- Client operations --------------------

    @Override
    public synchronized List<Client> getAllClients() {
        return clientDao.findAll();
    }

    @Override
    public synchronized void addClient(Client client) {
        clientDao.add(client);
    }

    @Override
    public synchronized Client findClientByName(String name) {
        return clientDao.findByName(name);
    }

    // -------------------- Reservation operations --------------------

    @Override
    public synchronized List<Reservation> getAllReservations() {
        return reservationDao.findAll();
    }

    @Override
    public synchronized Reservation makeReservation(Client client,
                                                    Room room,
                                                    LocalDate checkIn,
                                                    LocalDate checkOut) {
        // Recharger la chambre depuis la BD
        Room managedRoom = roomDao.getRoomById(room.getId());
        if (managedRoom == null || !managedRoom.isAvailable()) {
            return null;
        }

        // Marquer la chambre indisponible et sauvegarder
        managedRoom.setAvailable(false);
        roomDao.updateRoom(managedRoom);

        Reservation reservation = new Reservation();
        reservation.setClient(client);
        reservation.setRoom(managedRoom);
        reservation.setCheckInDate(checkIn);
        reservation.setCheckOutDate(checkOut);
        reservation.setConfirmed(false);

        return reservationDao.save(reservation);
    }

    @Override
    public synchronized void cancelReservation(int reservationId) {
        Reservation res = reservationDao.findById(reservationId);
        if (res != null) {
            Room room = res.getRoom();
            room.setAvailable(true);
            roomDao.updateRoom(room);
            reservationDao.delete(reservationId);
        }
    }

    @Override
    public synchronized void confirmReservation(int reservationId) {
        Reservation res = reservationDao.findById(reservationId);
        if (res != null) {
            res.setConfirmed(true);
            reservationDao.update(res);
        }
    }
}

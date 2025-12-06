package com.hotel.service.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

/**
 * Concrete implementation of {@link HotelService}.  This class
 * maintains simple in‑memory lists of rooms, clients and
 * reservations.  It does not persist data to any external storage and
 * is intended purely to illustrate the distribution of services using
 * RMI.  All methods are synchronised so that concurrent access from
 * multiple clients will be thread safe.
 */
public class HotelServiceImpl extends UnicastRemoteObject implements HotelService {
    private static final long serialVersionUID = 1L;

    private final List<Room> rooms;
    private final List<Client> clients;
    private final List<Reservation> reservations;
    private int nextRoomId = 1;
    private int nextClientId = 1;
    private int nextReservationId = 1;

    public HotelServiceImpl() throws RemoteException {
        super();
        this.rooms = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.reservations = new ArrayList<>();
        initialiseSampleData();
    }

    /**
     * Populates the service with a handful of rooms and clients so that
     * the application can be demonstrated without requiring the user to
     * create everything from scratch.  In a real system this method
     * would not exist and data would be loaded from a persistent
     * storage mechanism.
     */
    private void initialiseSampleData() {
        // add some rooms
        rooms.add(new Room(nextRoomId++, "Single", 50.0, true));
        rooms.add(new Room(nextRoomId++, "Double", 80.0, true));
        rooms.add(new Room(nextRoomId++, "Suite", 120.0, true));
        // add some clients
        clients.add(new Client(nextClientId++, "Alice", "1234 Avenue Street", "alice@example.com"));
        clients.add(new Client(nextClientId++, "Bob", "5678 Boulevard", "bob@example.com"));
    }

    // Room operations
    @Override
    public synchronized List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    @Override
    public synchronized List<Room> getAvailableRooms() {
        return rooms.stream().filter(Room::isAvailable).collect(Collectors.toList());
    }

    @Override
    public synchronized void addRoom(Room room) {
        room.setId(nextRoomId++);
        rooms.add(room);
    }

    @Override
    public synchronized void updateRoom(Room room) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId() == room.getId()) {
                rooms.set(i, room);
                return;
            }
        }
    }

    @Override
    public synchronized void deleteRoom(int id) {
        rooms.removeIf(r -> r.getId() == id);
    }

    // Client operations
    @Override
    public synchronized List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    @Override
    public synchronized void addClient(Client client) {
        client.setId(nextClientId++);
        clients.add(client);
    }

    @Override
    public synchronized Client findClientByName(String name) {
        return clients.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // Reservation operations
    @Override
    public synchronized List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    @Override
    public synchronized Reservation makeReservation(Client client, Room room, LocalDate checkIn, LocalDate checkOut) {
        // simple availability check: ensure room is available and not overlapping with existing reservations
        if (!room.isAvailable()) {
            return null;
        }
        // mark room as unavailable during the reservation period
        room.setAvailable(false);
        Reservation res = new Reservation(nextReservationId++, client, room, checkIn, checkOut);
        reservations.add(res);
        client.addReservation(res);
        return res;
    }

    @Override
    public synchronized void cancelReservation(int reservationId) {
        Reservation res = reservations.stream()
                .filter(r -> r.getId() == reservationId)
                .findFirst()
                .orElse(null);
        if (res != null) {
            res.getRoom().setAvailable(true);
            reservations.remove(res);
            res.getClient().getReservations().remove(res);
        }
    }

    @Override
    public synchronized void confirmReservation(int reservationId) {
        for (Reservation res : reservations) {
            if (res.getId() == reservationId) {
                res.setConfirmed(true);
                break;
            }
        }
    }
}
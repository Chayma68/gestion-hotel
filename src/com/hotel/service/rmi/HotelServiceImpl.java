package com.hotel.service.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

public class HotelServiceImpl extends UnicastRemoteObject implements HotelService {
    private static final long serialVersionUID = 1L;

    //  On initialise directement les listes, plus besoin de les réassigner dans le constructeur
    private final List<Client> clients = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Room> rooms = new ArrayList<>();

    private int nextRoomId = 1;
    private int nextClientId = 1;
    private int nextReservationId = 1;

    public HotelServiceImpl() throws RemoteException {
        super();
        initialiseSampleData();
    }


    private void initialiseSampleData() {
        //  On peut maintenant donner des numéros de chambre
        rooms.add(new Room(nextRoomId++, "101", "Single", 50.0, true));
        rooms.add(new Room(nextRoomId++, "102", "Double", 80.0, true));
        rooms.add(new Room(nextRoomId++, "201", "Suite", 120.0, true));

        // add some clients
        clients.add(new Client(nextClientId++, "Alice", "1234 Avenue Street", "alice@example.com"));
        clients.add(new Client(nextClientId++, "Bob", "5678 Boulevard", "bob@example.com"));
    }

    // -------------------- Room operations --------------------

    @Override
    public synchronized List<Room> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    @Override
    public synchronized List<Room> getAvailableRooms() {
        return rooms.stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
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

    // -------------------- Client operations --------------------

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

    // -------------------- Reservation operations --------------------

    @Override
    public synchronized List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    @Override
    public synchronized Reservation makeReservation(Client client, Room room,
                                                    LocalDate checkIn, LocalDate checkOut) {
        // simple availability check: ensure room is available
        if (!room.isAvailable()) {
            return null;
        }
        // mark room as unavailable during the reservation period
        room.setAvailable(false);
        Reservation res = new Reservation(nextReservationId++, client, room, checkIn, checkOut);
        reservations.add(res);
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

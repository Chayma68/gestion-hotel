package com.hotel.service.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

import com.hotel.model.Client;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

/**
 * Remote interface defining the basic hotel services such as room
 * management, client management and reservation management.  This
 * interface extends {@link java.rmi.Remote} so that its methods may
 * be invoked via RMI.  Each method declares {@link RemoteException}
 * to signal potential network or remote invocation failures.
 */
public interface HotelService extends Remote {
    // Room operations
    List<Room> getAllRooms() throws RemoteException;
    List<Room> getAvailableRooms() throws RemoteException;
    void addRoom(Room room) throws RemoteException;
    void updateRoom(Room room) throws RemoteException;
    void deleteRoom(int id) throws RemoteException;

    // Client operations
    List<Client> getAllClients() throws RemoteException;
    void addClient(Client client) throws RemoteException;
    Client findClientByName(String name) throws RemoteException;

    // Reservation operations
    List<Reservation> getAllReservations() throws RemoteException;
    Reservation makeReservation(Client client, Room room, LocalDate checkIn, LocalDate checkOut) throws RemoteException;
    void cancelReservation(int reservationId) throws RemoteException;
    void confirmReservation(int reservationId) throws RemoteException;
}
package com.hotel.db;

import com.hotel.model.Room;

import java.util.List;

public interface RoomDao {
    Room addRoom(Room room);
    void updateRoom(Room room);
    void deleteRoom(int roomId);
    List<Room> getAllRooms();
    List<Room> getAvailableRooms();
    Room getRoomById(int id);
}

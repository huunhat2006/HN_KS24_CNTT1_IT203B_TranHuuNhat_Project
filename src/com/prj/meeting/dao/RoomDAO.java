package com.prj.meeting.dao;

import com.prj.meeting.model.Room;
import java.util.List;
import java.util.Optional;

public interface RoomDAO {
    Optional<Room> findById(int id);
    List<Room> findAll();
    List<Room> findByStatus(String status);
    boolean save(Room room);
    boolean update(Room room);
    boolean delete(int id);
    List<Room> findAvailableRooms(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
}

package com.prj.meeting.service;

import com.prj.meeting.dao.RoomDAO;
import com.prj.meeting.dao.impl.RoomDAOImpl;
import com.prj.meeting.model.Room;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class RoomService {
    private final RoomDAO roomDAO;
    
    public RoomService() {
        this.roomDAO = new RoomDAOImpl();
    }
    
    public List<Room> getAllRooms() {
        return roomDAO.findAll();
    }
    
    public Optional<Room> getRoomById(int roomId) {
        return roomDAO.findById(roomId);
    }
    
    public boolean createRoom(Room room) {
        return roomDAO.save(room);
    }
    
    public boolean updateRoom(Room room) {
        return roomDAO.update(room);
    }
    
    public boolean deleteRoom(int roomId) {
        return roomDAO.delete(roomId);
    }
    
    public List<Room> getAvailableRooms() {
        return roomDAO.findByStatus("ACTIVE");
    }
    
    public List<Room> searchRoomsByName(String searchTerm) {
        List<Room> allRooms = roomDAO.findAll();
        List<Room> foundRooms = new ArrayList<>();
        
        for (Room room : allRooms) {
            if (room.getRoomName().toLowerCase().contains(searchTerm.toLowerCase())) {
                foundRooms.add(room);
            }
        }
        
        return foundRooms;
    }
    
    public boolean isRoomNameExists(String roomName) {
        List<Room> allRooms = roomDAO.findAll();
        for (Room room : allRooms) {
            if (room.getRoomName().equalsIgnoreCase(roomName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasBookings(int roomId) {
        try {
            var conn = com.prj.meeting.util.DBConnection.getInstance().getConnection();
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM bookings WHERE room_id = " + roomId);
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                stmt.close();
                return count > 0;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Room> getAvailableRoomsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<Room> allRooms = getAllRooms();
        List<Room> availableRooms = new ArrayList<>();
        BookingService bookingService = new BookingService();
        
        for (Room room : allRooms) {
            if ("ACTIVE".equals(room.getStatus()) && 
                bookingService.isRoomAvailable(room.getRoomId(), startTime, endTime)) {
                availableRooms.add(room);
            }
        }
        
        return availableRooms;
    }
    
    public List<Room> getAvailableRoomsNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        return getAvailableRoomsInTimeRange(now, oneHourLater);
    }
    
    public List<Room> getAvailableRoomsToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return getAvailableRoomsInTimeRange(startOfDay, endOfDay);
    }
}

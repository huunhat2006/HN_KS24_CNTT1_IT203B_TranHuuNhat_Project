package com.prj.meeting.dao.impl;

import com.prj.meeting.dao.BookingDAO;
import com.prj.meeting.model.Booking;
import com.prj.meeting.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingDAOImpl implements BookingDAO {
    
    @Override
    public Optional<Booking> findById(int id) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    @Override
    public List<Booking> findByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    @Override
    public List<Booking> findByRoomId(int roomId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE room_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    @Override
    public List<Booking> findByStatus(String status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    @Override
    public List<Booking> findBySupportStaffId(int supportStaffId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE support_staff_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, supportStaffId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    @Override
    public boolean save(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, room_id, support_staff_id, title, description, start_time, end_time, status, prep_status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            
            if (booking.getSupportStaffId() != null) {
                stmt.setInt(3, booking.getSupportStaffId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, booking.getTitle());
            stmt.setString(5, booking.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(7, Timestamp.valueOf(booking.getEndTime()));
            stmt.setString(8, booking.getStatus());
            stmt.setString(9, booking.getPrepStatus());
            stmt.setTimestamp(10, Timestamp.valueOf(booking.getCreatedAt() != null ? booking.getCreatedAt() : java.time.LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean update(Booking booking) {
        String sql = "UPDATE bookings SET user_id = ?, room_id = ?, support_staff_id = ?, title = ?, description = ?, start_time = ?, end_time = ?, status = ?, prep_status = ? WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getRoomId());
            
            if (booking.getSupportStaffId() != null) {
                stmt.setInt(3, booking.getSupportStaffId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, booking.getTitle());
            stmt.setString(5, booking.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(booking.getStartTime()));
            stmt.setTimestamp(7, Timestamp.valueOf(booking.getEndTime()));
            stmt.setString(8, booking.getStatus());
            stmt.setString(9, booking.getPrepStatus());
            stmt.setInt(10, booking.getBookingId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Booking> findBookingsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE (start_time <= ? AND end_time > ?) ORDER BY start_time";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(endTime));
            stmt.setTimestamp(2, Timestamp.valueOf(startTime));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }
    
    @Override
    public boolean addEquipmentToBooking(int bookingId, int equipmentId) {
        String sql = "INSERT INTO booking_equipments (booking_id, equipment_id, quantity) VALUES (?, ?, 1)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            stmt.setInt(2, equipmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean removeEquipmentFromBooking(int bookingId, int equipmentId) {
        String sql = "DELETE FROM booking_equipments WHERE booking_id = ? AND equipment_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            stmt.setInt(2, equipmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean addServiceToBooking(int bookingId, int serviceId) {
        String sql = "INSERT INTO booking_services (booking_id, service_id, quantity) VALUES (?, ?, 1)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            stmt.setInt(2, serviceId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean removeServiceFromBooking(int bookingId, int serviceId) {
        String sql = "DELETE FROM booking_services WHERE booking_id = ? AND service_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            stmt.setInt(2, serviceId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setRoomId(rs.getInt("room_id"));
        
        int supportStaffId = rs.getInt("support_staff_id");
        if (!rs.wasNull()) {
            booking.setSupportStaffId(supportStaffId);
        }
        
        booking.setTitle(rs.getString("title"));
        booking.setDescription(rs.getString("description"));
        booking.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        booking.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        booking.setStatus(rs.getString("status"));
        booking.setPrepStatus(rs.getString("prep_status"));
        booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return booking;
    }
}

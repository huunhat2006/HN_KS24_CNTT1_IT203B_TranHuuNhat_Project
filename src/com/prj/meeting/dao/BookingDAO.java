package com.prj.meeting.dao;

import com.prj.meeting.model.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingDAO {
    Optional<Booking> findById(int id);
    List<Booking> findAll();
    List<Booking> findByUserId(int userId);
    List<Booking> findByRoomId(int roomId);
    List<Booking> findByStatus(String status);
    List<Booking> findBySupportStaffId(int supportStaffId);
    boolean save(Booking booking);
    boolean update(Booking booking);
    boolean delete(int id);
    List<Booking> findBookingsInTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    boolean addEquipmentToBooking(int bookingId, int equipmentId);
    boolean removeEquipmentFromBooking(int bookingId, int equipmentId);
    boolean addServiceToBooking(int bookingId, int serviceId);
    boolean removeServiceFromBooking(int bookingId, int serviceId);
}

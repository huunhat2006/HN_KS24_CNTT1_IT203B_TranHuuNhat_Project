package com.prj.meeting.service;

import com.prj.meeting.dao.BookingDAO;
import com.prj.meeting.dao.impl.BookingDAOImpl;
import com.prj.meeting.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class BookingService {
    private final BookingDAO bookingDAO;
    
    public BookingService() {
        this.bookingDAO = new BookingDAOImpl();
    }
    
    public Optional<Booking> getBookingById(int bookingId) {
        return bookingDAO.findById(bookingId);
    }
    
    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }
    
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingDAO.findByUserId(userId);
    }
    
    public List<Booking> getBookingsByRoomId(int roomId) {
        return bookingDAO.findByRoomId(roomId);
    }
    
    public List<Booking> getBookingsByStatus(String status) {
        return bookingDAO.findByStatus(status);
    }
    
    public boolean createBooking(Booking booking) {
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(LocalDateTime.now());
        }

        if (booking.getTitle() == null || booking.getTitle().trim().isEmpty()) {
            booking.setTitle("Meeting");
        }
        if (booking.getDescription() == null) {
            booking.setDescription("");
        }
        
        if (!isRoomAvailable(booking.getRoomId(), booking.getStartTime(), booking.getEndTime())) {
            return false;
        }
        
        booking.setStatus("PENDING");
        booking.setPrepStatus("NOT_STARTED");
        
        return bookingDAO.save(booking);
    }
    
    public boolean createBookingWithEquipmentAndServices(Booking booking, List<Integer> equipmentIds, List<Integer> serviceIds) {
        if (!createBooking(booking)) {
            return false;
        }
        
        // Add equipment to booking
        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            for (Integer equipmentId : equipmentIds) {
                bookingDAO.addEquipmentToBooking(booking.getBookingId(), equipmentId);
            }
        }
        
        // Add services to booking
        if (serviceIds != null && !serviceIds.isEmpty()) {
            for (Integer serviceId : serviceIds) {
                bookingDAO.addServiceToBooking(booking.getBookingId(), serviceId);
            }
        }
        
        return true;
    }
    
    public boolean updateBooking(Booking booking) {
        return bookingDAO.update(booking);
    }
    
    public boolean cancelBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus("CANCELLED");
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public boolean approveBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus("APPROVED");
            booking.setPrepStatus("PREPARING");
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public boolean approveBookingWithSupport(int bookingId, int supportStaffId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus("APPROVED");
            booking.setPrepStatus("PREPARING");
            booking.setSupportStaffId(supportStaffId);
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public boolean rejectBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus("REJECTED");
            booking.setPrepStatus("CANCELLED");
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public List<Booking> getPendingBookings() {
        return bookingDAO.findByStatus("PENDING");
    }
    
    public List<Booking> getApprovedBookings() {
        return bookingDAO.findByStatus("APPROVED");
    }
    
    public List<Booking> getBookingsForSupportStaff(int supportStaffId) {
        return bookingDAO.findBySupportStaffId(supportStaffId);
    }
    
    public boolean updatePreparationStatus(int bookingId, String prepStatus) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setPrepStatus(prepStatus);
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public boolean markRoomReady(int bookingId) {
        return updatePreparationStatus(bookingId, "READY");
    }
    
    public boolean markEquipmentMissing(int bookingId) {
        return updatePreparationStatus(bookingId, "MISSING_EQUIPMENT");
    }
    
    public List<Booking> getEmployeeBookings(int userId) {
        return bookingDAO.findByUserId(userId);
    }
    
    public List<Booking> getEmployeeUpcomingBookings(int userId) {
        List<Booking> employeeBookings = getEmployeeBookings(userId);
        List<Booking> upcomingBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Booking booking : employeeBookings) {
            if (booking.getStartTime().isAfter(now) && 
                !"CANCELLED".equals(booking.getStatus()) && 
                !"REJECTED".equals(booking.getStatus())) {
                upcomingBookings.add(booking);
            }
        }
        
        return upcomingBookings;
    }
    
    public List<Booking> getEmployeeBookingsForToday(int userId) {
        List<Booking> employeeBookings = getEmployeeBookings(userId);
        List<Booking> todayBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        
        for (Booking booking : employeeBookings) {
            if ((booking.getStartTime().isAfter(startOfDay) || booking.getStartTime().isEqual(startOfDay)) &&
                (booking.getStartTime().isBefore(endOfDay) || booking.getStartTime().isEqual(endOfDay)) &&
                !"CANCELLED".equals(booking.getStatus()) && 
                !"REJECTED".equals(booking.getStatus())) {
                todayBookings.add(booking);
            }
        }
        
        return todayBookings;
    }
    
    public boolean isMeetingReady(int bookingId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            return "APPROVED".equals(booking.getStatus()) && "READY".equals(booking.getPrepStatus());
        }
        return false;
    }
    
    public String getMeetingStatus(int bookingId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            return "Status: " + booking.getStatus() + ", Preparation: " + booking.getPrepStatus();
        }
        return "Booking not found";
    }
    
    public List<Booking> getEmployeeReadyMeetings(int userId) {
        List<Booking> employeeBookings = getEmployeeBookings(userId);
        List<Booking> readyMeetings = new ArrayList<>();
        
        for (Booking booking : employeeBookings) {
            if (isMeetingReady(booking.getBookingId())) {
                readyMeetings.add(booking);
            }
        }
        
        return readyMeetings;
    }
    
    public boolean deleteBooking(int bookingId) {
        return bookingDAO.delete(bookingId);
    }
    
    public List<Booking> getBookingsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return bookingDAO.findBookingsInTimeRange(startTime, endTime);
    }
    
    public boolean isRoomAvailable(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            return false;
        }
        
        List<Booking> existingBookings = bookingDAO.findByRoomId(roomId);
        
        for (Booking booking : existingBookings) {
            if ("CANCELLED".equals(booking.getStatus()) || "REJECTED".equals(booking.getStatus())) {
                continue;
            }
            
            if (hasTimeConflict(startTime, endTime, booking.getStartTime(), booking.getEndTime())) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean hasTimeConflict(LocalDateTime newStart, LocalDateTime newEnd, 
                                  LocalDateTime existingStart, LocalDateTime existingEnd) {
        return !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd) || 
                 newStart.isEqual(existingEnd) || newEnd.isEqual(existingStart));
    }
    
    public List<Booking> getConflictingBookings(int roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> conflictingBookings = new ArrayList<>();
        List<Booking> existingBookings = bookingDAO.findByRoomId(roomId);
        
        for (Booking booking : existingBookings) {
            if ("CANCELLED".equals(booking.getStatus()) || "REJECTED".equals(booking.getStatus())) {
                continue;
            }
            
            if (hasTimeConflict(startTime, endTime, booking.getStartTime(), booking.getEndTime())) {
                conflictingBookings.add(booking);
            }
        }
        
        return conflictingBookings;
    }
}

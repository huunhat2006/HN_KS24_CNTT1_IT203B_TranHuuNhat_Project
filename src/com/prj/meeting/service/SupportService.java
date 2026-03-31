package com.prj.meeting.service;

import com.prj.meeting.dao.BookingDAO;
import com.prj.meeting.dao.impl.BookingDAOImpl;
import com.prj.meeting.model.Booking;
import com.prj.meeting.model.User;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class SupportService {
    private final BookingDAO bookingDAO;
    private final UserService userService;
    
    public SupportService() {
        this.bookingDAO = new BookingDAOImpl();
        this.userService = new UserService();
    }
    
    public List<Booking> getAssignedTasks(int supportStaffId) {
        return bookingDAO.findBySupportStaffId(supportStaffId);
    }
    
    public List<Booking> getTasksByStatus(int supportStaffId, String prepStatus) {
        List<Booking> assignedTasks = getAssignedTasks(supportStaffId);
        List<Booking> filteredTasks = new ArrayList<>();
        
        for (Booking task : assignedTasks) {
            if (prepStatus.equals(task.getPrepStatus())) {
                filteredTasks.add(task);
            }
        }
        
        return filteredTasks;
    }
    
    public List<Booking> getPreparingTasks(int supportStaffId) {
        return getTasksByStatus(supportStaffId, "PREPARING");
    }
    
    public List<Booking> getReadyTasks(int supportStaffId) {
        return getTasksByStatus(supportStaffId, "READY");
    }
    
    public List<Booking> getTasksWithMissingEquipment(int supportStaffId) {
        return getTasksByStatus(supportStaffId, "MISSING_EQUIPMENT");
    }
    
    public boolean updateTaskStatus(int bookingId, String prepStatus) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setPrepStatus(prepStatus);
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public boolean markTaskAsPreparing(int bookingId) {
        return updateTaskStatus(bookingId, "PREPARING");
    }
    
    public boolean markTaskAsReady(int bookingId) {
        return updateTaskStatus(bookingId, "READY");
    }
    
    public boolean reportMissingEquipment(int bookingId) {
        return updateTaskStatus(bookingId, "MISSING_EQUIPMENT");
    }
    
    public List<User> getAllSupportStaff() {
        return userService.getUsersByRole("SUPPORT");
    }
    
    public boolean assignSupportStaff(int bookingId, int supportStaffId) {
        Optional<Booking> bookingOpt = bookingDAO.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setSupportStaffId(supportStaffId);
            booking.setPrepStatus("PREPARING");
            return bookingDAO.update(booking);
        }
        return false;
    }
    
    public boolean reassignTask(int bookingId, int newSupportStaffId) {
        return assignSupportStaff(bookingId, newSupportStaffId);
    }
    
    public List<Booking> getUnassignedApprovedBookings() {
        List<Booking> approvedBookings = bookingDAO.findByStatus("APPROVED");
        List<Booking> unassignedTasks = new ArrayList<>();
        
        for (Booking booking : approvedBookings) {
            if (booking.getSupportStaffId() == null) {
                unassignedTasks.add(booking);
            }
        }
        
        return unassignedTasks;
    }
    
    public Optional<User> getSupportStaffDetails(int supportStaffId) {
        return userService.getUserById(supportStaffId);
    }
}

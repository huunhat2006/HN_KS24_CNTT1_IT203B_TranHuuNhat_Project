package com.prj.meeting.model;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private int bookingId;
    private int userId;
    private int roomId;
    private Integer supportStaffId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String prepStatus;
    private LocalDateTime createdAt;
    
    private User user;
    private Room room;
    private User supportStaff;
    private List<Equipment> equipments;
    private List<Service> services;
    
    public Booking() {}
    
    public Booking(int userId, int roomId, String title, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.userId = userId;
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "PENDING";
        this.prepStatus = "NOT_STARTED";
    }
    
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    
    public Integer getSupportStaffId() { return supportStaffId; }
    public void setSupportStaffId(Integer supportStaffId) { this.supportStaffId = supportStaffId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPrepStatus() { return prepStatus; }
    public void setPrepStatus(String prepStatus) { this.prepStatus = prepStatus; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    
    public User getSupportStaff() { return supportStaff; }
    public void setSupportStaff(User supportStaff) { this.supportStaff = supportStaff; }
    
    public List<Equipment> getEquipments() { return equipments; }
    public void setEquipments(List<Equipment> equipments) { this.equipments = equipments; }
    
    public List<Service> getServices() { return services; }
    public void setServices(List<Service> services) { this.services = services; }
    
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", supportStaffId=" + supportStaffId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", prepStatus='" + prepStatus + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

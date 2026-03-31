package com.prj.meeting.presentation;

import com.prj.meeting.SimpleMainApplication;
import com.prj.meeting.model.Booking;
import com.prj.meeting.model.User;
import com.prj.meeting.service.BookingService;
import com.prj.meeting.service.RoomService;
import com.prj.meeting.service.UserService;
import com.prj.meeting.presentation.SimpleConsoleUI;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SupportMenu {
    
    private static BookingService bookingService = new BookingService();
    private static RoomService roomService = new RoomService();
    private static UserService userService = new UserService();
    
    public static void showMenu(User currentUser) {
        while (true) {
            String[] options = {
                "Xem danh sách cuộc họp được phân công",
                "Cập nhật trạng thái chuẩn bị phòng",
                "Cập nhật thông tin cá nhân",
                "Đăng xuất"
            };
            
            SimpleConsoleUI.showMenu("MENU NHÂN VIÊN HỖ TRỢ", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        SupportMenu.viewAssignedBookings(currentUser);
                        break;
                    case 2:
                        SupportMenu.updatePreparationStatus(currentUser);
                        break;
                    case 3:
                        UserMenu.updateProfile(currentUser);
                        break;
                    case 4:
                        SimpleMainApplication.logout();
                        return;
                    default:
                        SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                }
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Vui lòng nhập số!");
            }
        }
    }
    
    public static void viewAssignedBookings(User currentUser) {
        SimpleConsoleUI.showHeader("DANH SÁCH CUỘC HỌP ĐƯỢC PHÂN CÔNG");
        
        List<Booking> bookings = bookingService.getBookingsForSupportStaff(currentUser.getUserId());
        
        if (bookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Bạn chưa được phân công hỗ trợ cuộc họp nào.");
            return;
        }

        String[] headers = {"ID", "Phòng", "Người đặt", "Bắt đầu", "Kết thúc", "Trạng thái", "Trạng thái chuẩn bị"};
        String[][] data = new String[bookings.size()][7];
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            
            var roomOpt = roomService.getRoomById(booking.getRoomId());
            String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
            
            var userOpt = userService.getUserById(booking.getUserId());
            String userName = userOpt.isPresent() ? userOpt.get().getFullName() : "Unknown";
            
            data[i][0] = String.valueOf(booking.getBookingId());
            data[i][1] = roomName;
            data[i][2] = userName;
            data[i][3] = booking.getStartTime().format(formatter);
            data[i][4] = booking.getEndTime().format(formatter);
            data[i][5] = booking.getStatus();
            data[i][6] = booking.getPrepStatus();
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    public static void updatePreparationStatus(User currentUser) {
        SimpleConsoleUI.showHeader("CẬP NHẬT TRẠNG THÁI CHUẨN BỊ");
        
        int bookingId = SimpleConsoleUI.getIntInput("Nhập ID cuộc họp: ");
        
        var bookingOpt = bookingService.getBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy cuộc họp với ID: " + bookingId);
            return;
        }
        
        Booking booking = bookingOpt.get();
        
        // Ensure this user is assigned to this booking
        if (booking.getSupportStaffId() == null || booking.getSupportStaffId() != currentUser.getUserId()) {
            SimpleConsoleUI.showError("Bạn không được phân công hỗ trợ cuộc họp này!");
            return;
        }
        
        String[] options = {"NOT_STARTED", "PREPARING", "READY", "MISSING_EQUIPMENT"};
        SimpleConsoleUI.showMenu("CHỌN TRẠNG THÁI CHUẨN BỊ", options);
        
        try {
            int statusChoice = Integer.parseInt(SimpleConsoleUI.getInput(""));
            if (statusChoice >= 1 && statusChoice <= options.length) {
                String newStatus = options[statusChoice - 1];
                
                boolean success = bookingService.updatePreparationStatus(bookingId, newStatus);
                if (success) {
                    SimpleConsoleUI.showSuccess("Cập nhật trạng thái thành công: " + newStatus);
                } else {
                    SimpleConsoleUI.showError("Cập nhật trạng thái thất bại!");
                }
            } else {
                SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số!");
        }
    }
}

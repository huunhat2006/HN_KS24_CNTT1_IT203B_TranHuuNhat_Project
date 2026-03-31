package com.prj.meeting.presentation;

import com.prj.meeting.model.Booking;
import com.prj.meeting.model.Room;
import com.prj.meeting.model.User;
import com.prj.meeting.service.BookingService;
import com.prj.meeting.service.RoomService;
import com.prj.meeting.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportManagementMenu {
    private static BookingService bookingService = new BookingService();
    private static RoomService roomService = new RoomService();
    private static UserService userService = new UserService();
    
    public static void showMenu() {
        while (true) {
            SimpleConsoleUI.showHeader("XEM BÁO CÁO");
            
            String[] options = {
                "Báo cáo sử dụng phòng họp",
                "Báo cáo đặt phòng theo người dùng",
                "Báo cáo đặt phòng theo phòng",
                "Báo cáo theo khoảng thời gian",
                "Thống kê tổng quan",
                "Quay lại menu chính"
            };
            
            SimpleConsoleUI.showMenu("CHỌN CHỨC NĂNG", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        roomUsageReport();
                        break;
                    case 2:
                        userBookingReport();
                        break;
                    case 3:
                        roomBookingReport();
                        break;
                    case 4:
                        timeRangeReport();
                        break;
                    case 5:
                        overviewStatistics();
                        break;
                    case 6:
                        return;
                    default:
                        SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                }
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Vui lòng nhập số!");
            }
        }
    }
    
    private static void roomUsageReport() {
        SimpleConsoleUI.showHeader("BÁO CÁO SỬ DỤNG PHÒNG HỌP");
        
        List<Room> rooms = roomService.getAllRooms();
        List<Booking> allBookings = bookingService.getAllBookings();
        
        if (rooms.isEmpty() || allBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đủ dữ liệu để tạo báo cáo.");
            return;
        }
        
        String[] headers = {"Phòng họp", "Tổng đặt phòng", "Đã duyệt", "Đã hoàn thành", "Bị từ chối", "Đã hủy", "Tỷ lệ thành công"};
        String[][] data = new String[rooms.size()][7];
        
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            List<Booking> roomBookings = allBookings.stream()
                .filter(b -> b.getRoomId() == room.getRoomId())
                .collect(Collectors.toList());
            
            int total = roomBookings.size();
            int approved = (int) roomBookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count();
            int completed = (int) roomBookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
            int rejected = (int) roomBookings.stream().filter(b -> "REJECTED".equals(b.getStatus())).count();
            int cancelled = (int) roomBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
            
            double successRate = total > 0 ? (double) completed / total * 100 : 0;
            
            data[i][0] = room.getRoomName();
            data[i][1] = String.valueOf(total);
            data[i][2] = String.valueOf(approved);
            data[i][3] = String.valueOf(completed);
            data[i][4] = String.valueOf(rejected);
            data[i][5] = String.valueOf(cancelled);
            data[i][6] = String.format("%.1f%%", successRate);
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void userBookingReport() {
        SimpleConsoleUI.showHeader("BÁO CÁO ĐẶT PHÒNG THEO NGƯỜI DÙNG");
        
        List<User> users = userService.getAllUsers();
        List<Booking> allBookings = bookingService.getAllBookings();
        
        if (users.isEmpty() || allBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đủ dữ liệu để tạo báo cáo.");
            return;
        }
        
        String[] headers = {"Người dùng", "Phòng ban", "Tổng đặt phòng", "Đã duyệt", "Đã hoàn thành", "Bị từ chối", "Đã hủy"};
        String[][] data = new String[users.size()][7];
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            List<Booking> userBookings = allBookings.stream()
                .filter(b -> b.getUserId() == user.getUserId())
                .collect(Collectors.toList());
            
            int total = userBookings.size();
            int approved = (int) userBookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count();
            int completed = (int) userBookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
            int rejected = (int) userBookings.stream().filter(b -> "REJECTED".equals(b.getStatus())).count();
            int cancelled = (int) userBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
            
            data[i][0] = user.getFullName();
            data[i][1] = user.getDepartment();
            data[i][2] = String.valueOf(total);
            data[i][3] = String.valueOf(approved);
            data[i][4] = String.valueOf(completed);
            data[i][5] = String.valueOf(rejected);
            data[i][6] = String.valueOf(cancelled);
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void roomBookingReport() {
        SimpleConsoleUI.showHeader("BÁO CÁO ĐẶT PHÒNG THEO PHÒNG");
        
        List<Room> rooms = roomService.getAllRooms();
        List<Booking> allBookings = bookingService.getAllBookings();
        
        if (rooms.isEmpty() || allBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đủ dữ liệu để tạo báo cáo.");
            return;
        }
        
        for (Room room : rooms) {
            List<Booking> roomBookings = allBookings.stream()
                .filter(b -> b.getRoomId() == room.getRoomId())
                .collect(Collectors.toList());
            
            if (roomBookings.isEmpty()) {
                continue;
            }
            
            SimpleConsoleUI.showMessage("=== PHÒNG: " + room.getRoomName() + " ===");
            
            String[] headers = {"ID Đặt phòng", "Người dùng", "Bắt đầu", "Kết thúc", "Trạng thái"};
            String[][] data = new String[roomBookings.size()][5];
            
            for (int i = 0; i < roomBookings.size(); i++) {
                Booking booking = roomBookings.get(i);
                var userOpt = userService.getUserById(booking.getUserId());
                
                String userName = userOpt.isPresent() ? userOpt.get().getFullName() : "Unknown";
                
                data[i][0] = String.valueOf(booking.getBookingId());
                data[i][1] = userName;
                data[i][2] = booking.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][3] = booking.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][4] = getStatusDisplay(booking.getStatus());
            }
            
            SimpleConsoleUI.showTable(headers, data);
            SimpleConsoleUI.showMessage("");
        }
    }
    
    private static void timeRangeReport() {
        SimpleConsoleUI.showHeader("BÁO CÁO THEO KHOẢNG THỜI GIAN");
        
        try {
            String startDateStr = SimpleConsoleUI.getInput("Nhập ngày bắt đầu (yyyy-MM-dd): ");
            String endDateStr = SimpleConsoleUI.getInput("Nhập ngày kết thúc (yyyy-MM-dd): ");
            
            LocalDateTime startDateTime = LocalDateTime.parse(startDateStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endDateTime = LocalDateTime.parse(endDateStr + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            List<Booking> rangeBookings = bookingService.getBookingsInTimeRange(startDateTime, endDateTime);
            
            if (rangeBookings.isEmpty()) {
                SimpleConsoleUI.showMessage("Không có đặt phòng nào trong khoảng thời gian đã chọn.");
                return;
            }
            
            SimpleConsoleUI.showMessage("BÁO CÁO TỪ " + startDateStr + " ĐẾN " + endDateStr);
            SimpleConsoleUI.showMessage("Tổng số đặt phòng: " + rangeBookings.size());
            SimpleConsoleUI.showMessage("");
            
            String[] headers = {"ID", "Phòng", "Người dùng", "Bắt đầu", "Kết thúc", "Trạng thái"};
            String[][] data = new String[rangeBookings.size()][6];
            
            for (int i = 0; i < rangeBookings.size(); i++) {
                Booking booking = rangeBookings.get(i);
                var userOpt = userService.getUserById(booking.getUserId());
                var roomOpt = roomService.getRoomById(booking.getRoomId());
                
                String userName = userOpt.isPresent() ? userOpt.get().getFullName() : "Unknown";
                String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
                
                data[i][0] = String.valueOf(booking.getBookingId());
                data[i][1] = roomName;
                data[i][2] = userName;
                data[i][3] = booking.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][4] = booking.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][5] = getStatusDisplay(booking.getStatus());
            }
            
            SimpleConsoleUI.showTable(headers, data);
            SimpleConsoleUI.showMessage("");
            
        } catch (Exception e) {
            SimpleConsoleUI.showError("Định dạng ngày không hợp lệ! Vui lòng nhập theo định dạng yyyy-MM-dd");
        }
    }
    
    private static void overviewStatistics() {
        SimpleConsoleUI.showHeader("THỐNG KÊ TỔNG QUAN");
        
        List<User> users = userService.getAllUsers();
        List<Room> rooms = roomService.getAllRooms();
        List<Booking> allBookings = bookingService.getAllBookings();
        
        SimpleConsoleUI.showMessage("=== THỐNG KÊ HỆ THỐNG ===");
        SimpleConsoleUI.showMessage("Tổng số người dùng: " + users.size());
        SimpleConsoleUI.showMessage("Tổng số phòng họp: " + rooms.size());
        SimpleConsoleUI.showMessage("Tổng số đặt phòng: " + allBookings.size());
        SimpleConsoleUI.showMessage("");
        
        if (!allBookings.isEmpty()) {
            int pending = (int) allBookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
            int approved = (int) allBookings.stream().filter(b -> "APPROVED".equals(b.getStatus())).count();
            int completed = (int) allBookings.stream().filter(b -> "COMPLETED".equals(b.getStatus())).count();
            int rejected = (int) allBookings.stream().filter(b -> "REJECTED".equals(b.getStatus())).count();
            int cancelled = (int) allBookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();
            
            SimpleConsoleUI.showMessage("=== THỐNG KÊ ĐẶT PHÒNG ===");
            SimpleConsoleUI.showMessage("Chờ duyệt: " + pending);
            SimpleConsoleUI.showMessage("Đã duyệt: " + approved);
            SimpleConsoleUI.showMessage("Đã hoàn thành: " + completed);
            SimpleConsoleUI.showMessage("Bị từ chối: " + rejected);
            SimpleConsoleUI.showMessage("Đã hủy: " + cancelled);
            SimpleConsoleUI.showMessage("");
            
            double successRate = (double) completed / allBookings.size() * 100;
            SimpleConsoleUI.showMessage("Tỷ lệ thành công: " + String.format("%.1f%%", successRate));
            SimpleConsoleUI.showMessage("");
            
            Map<String, Long> bookingsByRole = users.stream()
                .collect(Collectors.toMap(
                    User::getRole,
                    user -> allBookings.stream().filter(b -> b.getUserId() == user.getUserId()).count(),
                    Long::sum
                ));
            
            SimpleConsoleUI.showMessage("=== THỐNG KÊ THEO VAI TRÒ ===");
            bookingsByRole.forEach((role, count) -> {
                String roleDisplay = getRoleDisplay(role);
                SimpleConsoleUI.showMessage(roleDisplay + ": " + count);
            });
            
            SimpleConsoleUI.showMessage("");
            
            Map<String, Long> bookingsByDepartment = users.stream()
                .collect(Collectors.toMap(
                    User::getDepartment,
                    user -> allBookings.stream().filter(b -> b.getUserId() == user.getUserId()).count(),
                    Long::sum
                ));
            
            SimpleConsoleUI.showMessage("=== THỐNG KÊ THEO PHÒNG BAN ===");
            bookingsByDepartment.forEach((department, count) -> {
                SimpleConsoleUI.showMessage(department + ": " + count);
            });
        }
        
        SimpleConsoleUI.showMessage("");
    }
    
    private static String getStatusDisplay(String status) {
        switch (status) {
            case "PENDING": return "CHỜ DUYỆT";
            case "APPROVED": return "ĐÃ DUYỆT";
            case "REJECTED": return "BỊ TỪ CHỐI";
            case "CANCELLED": return "ĐÃ HỦY";
            case "COMPLETED": return "ĐÃ HOÀN THÀNH";
            default: return status;
        }
    }
    
    private static String getRoleDisplay(String role) {
        switch (role) {
            case "ADMIN": return "Quản trị viên";
            case "EMPLOYEE": return "Nhân viên";
            case "SUPPORT_STAFF": return "Nhân viên hỗ trợ";
            default: return role;
        }
    }
}

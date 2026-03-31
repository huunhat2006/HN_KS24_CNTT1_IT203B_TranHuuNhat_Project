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

public class BookingManagementMenu {
    private static BookingService bookingService = new BookingService();
    private static RoomService roomService = new RoomService();
    private static UserService userService = new UserService();
    
    public static void showMenu() {
        while (true) {
            SimpleConsoleUI.showHeader("QUẢN LÝ ĐẶT PHÒNG");
            
            String[] options = {
                "Xem tất cả đặt phòng",
                "Xem đặt phòng theo trạng thái",
                "Xem đặt phòng theo người dùng",
                "Phê duyệt đặt phòng",
                "Từ chối đặt phòng",
                "Hủy đặt phòng",
                "Phân công nhân viên hỗ trợ",
                "Quay lại menu chính"
            };
            
            SimpleConsoleUI.showMenu("CHỌN CHỨC NĂNG", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        viewAllBookings();
                        break;
                    case 2:
                        viewBookingsByStatus();
                        break;
                    case 3:
                        viewBookingsByUser();
                        break;
                    case 4:
                        approveBooking();
                        break;
                    case 5:
                        rejectBooking();
                        break;
                    case 6:
                        cancelBooking();
                        break;
                    case 7:
                        assignSupportStaff();
                        break;
                    case 8:
                        return;
                    default:
                        SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                }
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Vui lòng nhập số!");
            }
        }
    }
    
    private static void viewAllBookings() {
        SimpleConsoleUI.showHeader("DANH SÁCH TẤT CẢ ĐẶT PHÒNG");
        
        List<Booking> bookings = bookingService.getAllBookings();
        
        if (bookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đặt phòng nào.");
            return;
        }
        
        displayBookingTable(bookings);
    }
    
    private static void viewBookingsByStatus() {
        SimpleConsoleUI.showHeader("XEM ĐẶT PHÒNG THEO TRẠNG THÁI");
        
        SimpleConsoleUI.showMessage("Chọn trạng thái:");
        String[] statusOptions = {
            "1. CHỜ DUYỆT (PENDING)",
            "2. ĐÃ DUYỆT (APPROVED)",
            "3. BỊ TỪ CHỐI (REJECTED)",
            "4. ĐÃ HỦY (CANCELLED)",
            "5. ĐÃ HOÀN THÀNH (COMPLETED)"
        };
        SimpleConsoleUI.showMenu("", statusOptions);
        
        try {
            int statusChoice = Integer.parseInt(SimpleConsoleUI.getInput(""));
            String status = "";
            
            switch (statusChoice) {
                case 1: status = "PENDING"; break;
                case 2: status = "APPROVED"; break;
                case 3: status = "REJECTED"; break;
                case 4: status = "CANCELLED"; break;
                case 5: status = "COMPLETED"; break;
                default:
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                    return;
            }
            
            List<Booking> bookings = bookingService.getBookingsByStatus(status);
            
            if (bookings.isEmpty()) {
                SimpleConsoleUI.showMessage("Không có đặt phòng nào với trạng thái đã chọn.");
                return;
            }
            
            displayBookingTable(bookings);
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số!");
        }
    }
    
    private static void viewBookingsByUser() {
        SimpleConsoleUI.showHeader("XEM ĐẶT PHÒNG THEO NGƯỜI DÙNG");
        
        try {
            int userId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID người dùng: "));
            
            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                SimpleConsoleUI.showError("Không tìm thấy người dùng với ID: " + userId);
                return;
            }
            
            User user = userOpt.get();
            List<Booking> bookings = bookingService.getBookingsByUserId(userId);
            
            if (bookings.isEmpty()) {
                SimpleConsoleUI.showMessage("Người dùng " + user.getFullName() + " chưa có đặt phòng nào.");
                return;
            }
            
            SimpleConsoleUI.showMessage("Đặt phòng của: " + user.getFullName() + " (" + user.getUsername() + ")");
            displayBookingTable(bookings);
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
        }
    }
    
    private static void approveBooking() {
        SimpleConsoleUI.showHeader("PHÊ DUYỆT ĐẶT PHÒNG");
        
        List<Booking> pendingBookings = bookingService.getBookingsByStatus("PENDING");
        
        if (pendingBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đặt phòng nào chờ duyệt.");
            return;
        }
        
        SimpleConsoleUI.showMessage("DANH SÁCH ĐẶT PHÒNG CHỜ DUYỆT:");
        displayBookingTable(pendingBookings);
        
        try {
            int bookingId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID đặt phòng cần phê duyệt: "));
            
            boolean success = bookingService.approveBooking(bookingId);
            if (success) {
                SimpleConsoleUI.showSuccess("Phê duyệt đặt phòng thành công!");
            } else {
                SimpleConsoleUI.showError("Phê duyệt đặt phòng thất bại! ID không tồn tại.");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
        }
    }
    
    private static void rejectBooking() {
        SimpleConsoleUI.showHeader("TỪ CHỐI ĐẶT PHÒNG");
        
        List<Booking> pendingBookings = bookingService.getBookingsByStatus("PENDING");
        
        if (pendingBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đặt phòng nào chờ duyệt.");
            return;
        }
        
        SimpleConsoleUI.showMessage("DANH SÁCH ĐẶT PHÒNG CHỜ DUYỆT:");
        displayBookingTable(pendingBookings);
        
        try {
            int bookingId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID đặt phòng cần từ chối: "));
            
            String confirm = SimpleConsoleUI.getInput("Bạn có chắc chắn muốn từ chối đặt phòng này? (y/N): ");
            if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                boolean success = bookingService.rejectBooking(bookingId);
                if (success) {
                    SimpleConsoleUI.showSuccess("Từ chối đặt phòng thành công!");
                } else {
                    SimpleConsoleUI.showError("Từ chối đặt phòng thất bại! ID không tồn tại.");
                }
            } else {
                SimpleConsoleUI.showMessage("Đã hủy thao tác.");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
        }
    }
    
    private static void cancelBooking() {
        SimpleConsoleUI.showHeader("HỦY ĐẶT PHÒNG");
        
        try {
            int bookingId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID đặt phòng cần hủy: "));
            
            var bookingOpt = bookingService.getBookingById(bookingId);
            if (bookingOpt.isEmpty()) {
                SimpleConsoleUI.showError("Không tìm thấy đặt phòng với ID: " + bookingId);
                return;
            }
            
            Booking booking = bookingOpt.get();
            SimpleConsoleUI.showMessage("Thông tin đặt phòng:");
            SimpleConsoleUI.showMessage("ID: " + booking.getBookingId());
            SimpleConsoleUI.showMessage("Trạng thái: " + getStatusDisplay(booking.getStatus()));
            SimpleConsoleUI.showMessage("");
            
            String confirm = SimpleConsoleUI.getInput("Bạn có chắc chắn muốn hủy đặt phòng này? (y/N): ");
            if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                boolean success = bookingService.cancelBooking(bookingId);
                if (success) {
                    SimpleConsoleUI.showSuccess("Hủy đặt phòng thành công!");
                } else {
                    SimpleConsoleUI.showError("Hủy đặt phòng thất bại!");
                }
            } else {
                SimpleConsoleUI.showMessage("Đã hủy thao tác.");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
        }
    }
    
    private static void assignSupportStaff() {
        SimpleConsoleUI.showHeader("PHÂN CÔNG NHÂN VIÊN HỖ TRỢ");
        
        List<Booking> approvedBookings = bookingService.getBookingsByStatus("APPROVED");
        
        if (approvedBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có đặt phòng nào đã được duyệt.");
            return;
        }
        
        SimpleConsoleUI.showMessage("DANH SÁCH ĐẶT PHÒNG ĐÃ DUYỆT:");
        displayBookingTable(approvedBookings);
        
        try {
            int bookingId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID đặt phòng cần phân công: "));
            
            var bookingOpt = bookingService.getBookingById(bookingId);
            if (bookingOpt.isEmpty()) {
                SimpleConsoleUI.showError("Không tìm thấy đặt phòng với ID: " + bookingId);
                return;
            }
            
            List<User> supportStaff = userService.getUsersByRole("SUPPORT_STAFF");
            if (supportStaff.isEmpty()) {
                SimpleConsoleUI.showError("Không có nhân viên hỗ trợ nào trong hệ thống.");
                return;
            }
            
            SimpleConsoleUI.showMessage("DANH SÁCH NHÂN VIÊN HỖ TRỢ:");
            String[] headers = {"ID", "Tên đăng nhập", "Họ tên", "Phòng ban"};
            String[][] data = new String[supportStaff.size()][4];
            
            for (int i = 0; i < supportStaff.size(); i++) {
                User staff = supportStaff.get(i);
                data[i][0] = String.valueOf(staff.getUserId());
                data[i][1] = staff.getUsername();
                data[i][2] = staff.getFullName();
                data[i][3] = staff.getDepartment();
            }
            
            SimpleConsoleUI.showTable(headers, data);
            
            int staffId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID nhân viên hỗ trợ: "));
            
            var staffOpt = userService.getUserById(staffId);
            if (staffOpt.isEmpty() || !"SUPPORT_STAFF".equals(staffOpt.get().getRole())) {
                SimpleConsoleUI.showError("Không tìm thấy nhân viên hỗ trợ với ID: " + staffId);
                return;
            }
            
            Booking booking = bookingOpt.get();
            booking.setSupportStaffId(staffId);
            booking.setPrepStatus("NOT_STARTED");
            
            boolean success = bookingService.updateBooking(booking);
            if (success) {
                SimpleConsoleUI.showSuccess("Phân công nhân viên hỗ trợ thành công!");
            } else {
                SimpleConsoleUI.showError("Phân công nhân viên hỗ trợ thất bại!");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
        }
    }
    
    private static void displayBookingTable(List<Booking> bookings) {
        String[] headers = {"ID", "Người dùng", "Phòng", "Bắt đầu", "Kết thúc", "Trạng thái", "Trạng thái chuẩn bị", "NV Hỗ trợ"};
        String[][] data = new String[bookings.size()][8];
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            
            var userOpt = userService.getUserById(booking.getUserId());
            var roomOpt = roomService.getRoomById(booking.getRoomId());
            
            String userName = userOpt.isPresent() ? userOpt.get().getFullName() : "Unknown";
            String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
            String supportStaffName = "";
            
            if (booking.getSupportStaffId() != null) {
                var staffOpt = userService.getUserById(booking.getSupportStaffId());
                supportStaffName = staffOpt.isPresent() ? staffOpt.get().getFullName() : "Unknown";
            }
            
            data[i][0] = String.valueOf(booking.getBookingId());
            data[i][1] = userName;
            data[i][2] = roomName;
            data[i][3] = booking.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][4] = booking.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][5] = getStatusDisplay(booking.getStatus());
            data[i][6] = getPrepStatusDisplay(booking.getPrepStatus());
            data[i][7] = supportStaffName.isEmpty() ? "Chưa phân công" : supportStaffName;
        }
        
        SimpleConsoleUI.showTable(headers, data);
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
    
    private static String getPrepStatusDisplay(String prepStatus) {
        switch (prepStatus) {
            case "NOT_STARTED": return "CHƯA BẮT ĐẦU";
            case "IN_PROGRESS": return "ĐANG CHUẨN BỊ";
            case "READY": return "SẴN SÀNG";
            case "COMPLETED": return "HOÀN THÀNH";
            default: return prepStatus;
        }
    }
}

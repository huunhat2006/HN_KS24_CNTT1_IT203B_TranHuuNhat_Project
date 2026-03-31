package com.prj.meeting;

import com.prj.meeting.model.User;
import com.prj.meeting.model.Room;
import com.prj.meeting.model.Booking;
import com.prj.meeting.presentation.RoomManagementMenu;
import com.prj.meeting.presentation.EquipmentManagementMenu;
import com.prj.meeting.presentation.ServiceManagementMenu;
import com.prj.meeting.presentation.UserManagementMenu;
import com.prj.meeting.presentation.BookingManagementMenu;
import com.prj.meeting.presentation.ReportManagementMenu;
import com.prj.meeting.presentation.SimpleConsoleUI;
import com.prj.meeting.service.UserService;
import com.prj.meeting.service.RoomService;
import com.prj.meeting.service.BookingService;

import java.util.List;
import java.util.ArrayList;

public class SimpleMainApplication {
    private static UserService userService = new UserService();
    private static RoomService roomService = new RoomService();
    private static BookingService bookingService = new BookingService();
    private static User currentUser = null;
    
    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }
    
    private static void showLoginMenu() {
        SimpleConsoleUI.showHeader("HỆ THỐNG ĐĂNG NHẬP");
        
        String[] options = {
            "Đăng nhập",
            "Đăng ký (Nhân viên)",
            "Thoát"
        };
        
        SimpleConsoleUI.showMenu("CHỌN CHỨC NĂNG", options);
        
        try {
            int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
            
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    SimpleConsoleUI.showMessage("Cảm ơn bạn đã sử dụng hệ thống!");
                    System.exit(0);
                    break;
                default:
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số!");
        }
    }
    
    private static void login() {
        SimpleConsoleUI.showHeader("ĐĂNG NHẬP");
        
        String username = SimpleConsoleUI.getInput("Tên đăng nhập: ");
        String password = SimpleConsoleUI.getInput("Mật khẩu: ");
        
        if (username.isEmpty() || password.isEmpty()) {
            SimpleConsoleUI.showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        
        var userOpt = userService.authenticate(username, password);
        
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            SimpleConsoleUI.showSuccess("Đăng nhập thành công! Chào mừng " + currentUser.getFullName());
        } else {
            SimpleConsoleUI.showError("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
    }
    
    private static void register() {
        SimpleConsoleUI.showHeader("ĐĂNG KÝ TÀI KHOẢN");
        
        String username = SimpleConsoleUI.getInput("Tên đăng nhập: ");
        if (username.isEmpty()) {
            SimpleConsoleUI.showError("Tên đăng nhập không được để trống!");
            return;
        }
        
        String password = SimpleConsoleUI.getInput("Mật khẩu: ");
        String confirmPassword = SimpleConsoleUI.getInput("Xác nhận mật khẩu: ");
        
        if (password.isEmpty()) {
            SimpleConsoleUI.showError("Mật khẩu không được để trống!");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            SimpleConsoleUI.showError("Mật khẩu xác nhận không khớp!");
            return;
        }
        
        String fullName = SimpleConsoleUI.getInput("Họ và tên: ");
        if (fullName.isEmpty()) {
            SimpleConsoleUI.showError("Họ và tên không được để trống!");
            return;
        }
        
        String department = SimpleConsoleUI.getInput("Phòng ban: ");
        String phoneNumber = SimpleConsoleUI.getInput("Số điện thoại: ");
        String email = SimpleConsoleUI.getInput("Email: ");
        
        // Validate email format
        if (!email.isEmpty() && !isValidEmail(email)) {
            SimpleConsoleUI.showError("Email không đúng định dạng!");
            return;
        }
        
        // Validate phone number format
        if (!phoneNumber.isEmpty() && !isValidPhoneNumber(phoneNumber)) {
            SimpleConsoleUI.showError("Số điện thoại không đúng định dạng!");
            return;
        }
        
        boolean success = userService.registerUser(
            username, password, "EMPLOYEE", fullName, department, phoneNumber, email
        );
        
        if (success) {
            SimpleConsoleUI.showSuccess("Đăng ký thành công!");
        } else {
            SimpleConsoleUI.showError("Đăng ký thất bại! Tên đăng nhập có thể đã tồn tại.");
        }
    }
    
    private static void showMainMenu() {
        SimpleConsoleUI.showHeader("MENU CHÍNH - " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        
        switch (currentUser.getRole()) {
            case "ADMIN":
                showAdminMenu();
                break;
            case "EMPLOYEE":
                showEmployeeMenu();
                break;
            case "SUPPORT_STAFF":
                showSupportMenu();
                break;
        }
    }
    
    private static void showAdminMenu() {
        String[] options = {
            "Quản lý Phòng họp",
            "Quản lý Thiết bị",
            "Quản lý Dịch vụ",
            "Quản lý Người dùng",
            "Quản lý Đặt phòng",
            "Xem Báo cáo",
            "Đăng xuất"
        };
        
        SimpleConsoleUI.showMenu("MENU QUẢN TRỊ VIÊN", options);
        
        try {
            int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
            
            switch (choice) {
                case 1:
                    RoomManagementMenu.showMenu();
                    break;
                case 2:
                    EquipmentManagementMenu.showMenu();
                    break;
                case 3:
                    ServiceManagementMenu.showMenu();
                    break;
                case 4:
                    UserManagementMenu.showMenu();
                    break;
                case 5:
                    BookingManagementMenu.showMenu();
                    break;
                case 6:
                    ReportManagementMenu.showMenu();
                    break;
                case 7:
                    logout();
                    return;
                default:
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số!");
        }
    }
    
    private static void showEmployeeMenu() {
        String[] options = {
            "Đặt phòng họp",
            "Xem lịch họp của tôi",
            "Hủy lịch họp",
            "Cập nhật thông tin cá nhân",
            "Xem phòng trống",
            "Đăng xuất"
        };
        
        SimpleConsoleUI.showMenu("MENU NHÂN VIÊN", options);
        
        try {
            int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
            
            switch (choice) {
                case 1:
                    com.prj.meeting.presentation.BookingMenu.showBookingMenu(currentUser);
                    break;
                case 2:
                    com.prj.meeting.presentation.BookingMenu.viewMyBookings(currentUser);
                    break;
                case 3:
                    com.prj.meeting.presentation.BookingMenu.cancelBooking(currentUser);
                    break;
                case 4:
                    updateProfile();
                    break;
                case 5:
                    viewAvailableRooms();
                    break;
                case 6:
                    logout();
                    return;
                default:
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số!");
        }
    }
    
    private static void showSupportMenu() {
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
                    viewAssignedMeetings();
                    break;
                case 2:
                    updateRoomPreparationStatus();
                    break;
                case 3:
                    updateProfile();
                    break;
                case 4:
                    logout();
                    return;
                default:
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số!");
        }
    }
    
    private static void viewMyBookings() {
        SimpleConsoleUI.showHeader("LỊCH HỌP CỦA TÔI");
        
        List<Booking> myBookings = bookingService.getBookingsByUserId(currentUser.getUserId());
        
        if (myBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Bạn chưa có lịch họp nào.");
            return;
        }
        
        String[] headers = {"ID", "Phòng", "Bắt đầu", "Kết thúc", "Trạng thái", "Trạng thái chuẩn bị"};
        String[][] data = new String[myBookings.size()][6];
        
        for (int i = 0; i < myBookings.size(); i++) {
            Booking booking = myBookings.get(i);
            var roomOpt = roomService.getRoomById(booking.getRoomId());
            String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
            
            data[i][0] = String.valueOf(booking.getBookingId());
            data[i][1] = roomName;
            data[i][2] = booking.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][3] = booking.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][4] = getStatusDisplay(booking.getStatus());
            data[i][5] = getPrepStatusDisplay(booking.getPrepStatus());
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void viewAvailableRooms() {
        SimpleConsoleUI.showHeader("PHÒNG TRỐNG");
        
        List<Room> availableRooms = roomService.getAvailableRooms();
        
        if (availableRooms.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có phòng trống nào.");
            return;
        }
        
        String[] headers = {"ID", "Tên phòng", "Sức chứa", "Vị trí", "Thiết bị cố định"};
        String[][] data = new String[availableRooms.size()][5];
        
        for (int i = 0; i < availableRooms.size(); i++) {
            Room room = availableRooms.get(i);
            data[i][0] = String.valueOf(room.getRoomId());
            data[i][1] = room.getRoomName();
            data[i][2] = String.valueOf(room.getCapacity());
            data[i][3] = room.getLocation();
            data[i][4] = room.getFixedEquipment() != null ? room.getFixedEquipment() : "";
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    public static void logout() {
        currentUser = null;
        SimpleConsoleUI.showSuccess("Đăng xuất thành công!");
    }
    
    public static User getCurrentUser() {
        return currentUser;
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
    
    private static void updateProfile() {
        SimpleConsoleUI.showHeader("CẬP NHẬT HỒ SƠ CÁ NHÂN");
        
        SimpleConsoleUI.showMessage("THÔNG TIN HIỆN TẠI:");
        SimpleConsoleUI.showMessage("================================");
        SimpleConsoleUI.showMessage("Họ và tên: " + currentUser.getFullName());
        SimpleConsoleUI.showMessage("Vai trò: " + getRoleDisplay(currentUser.getRole()));
        SimpleConsoleUI.showMessage("Phòng ban: " + (currentUser.getDepartment() != null ? currentUser.getDepartment() : "Chưa cập nhật"));
        SimpleConsoleUI.showMessage("Số điện thoại: " + (currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Chưa cập nhật"));
        SimpleConsoleUI.showMessage("Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Chưa cập nhật"));
        SimpleConsoleUI.showMessage("================================");
        SimpleConsoleUI.showMessage("");
        
        SimpleConsoleUI.getInput("Nhấn Enter để bắt đầu cập nhật thông tin...");
        SimpleConsoleUI.showMessage("");
        
        SimpleConsoleUI.showMessage("CẬP NHẬT THÔNG TIN(Để trống nếu không muốn thay đổi) :");
        
        String fullName = SimpleConsoleUI.getInput("Họ và tên mới: ");
        String department = SimpleConsoleUI.getInput("Phòng ban mới: ");
        String phoneNumber = SimpleConsoleUI.getInput("Số điện thoại mới: ");
        String email = SimpleConsoleUI.getInput("Email mới: ");
        
        // Validate email format if provided
        if (!email.isEmpty() && !isValidEmail(email)) {
            SimpleConsoleUI.showError("Email không đúng định dạng!");
            return;
        }
        
        // Validate phone number format if provided
        if (!phoneNumber.isEmpty() && !isValidPhoneNumber(phoneNumber)) {
            SimpleConsoleUI.showError("Số điện thoại không đúng định dạng!");
            return;
        }
        
        // Update user information
        boolean hasChanges = false;
        if (!fullName.isEmpty()) {
            currentUser.setFullName(fullName);
            hasChanges = true;
        }
        if (!department.isEmpty()) {
            currentUser.setDepartment(department);
            hasChanges = true;
        }
        if (!phoneNumber.isEmpty()) {
            currentUser.setPhoneNumber(phoneNumber);
            hasChanges = true;
        }
        if (!email.isEmpty()) {
            currentUser.setEmail(email);
            hasChanges = true;
        }
        
        if (!hasChanges) {
            SimpleConsoleUI.showMessage("Không có thay đổi nào được thực hiện.");
            return;
        }
        
        boolean success = userService.updateUser(currentUser);
        
        if (success) {
            SimpleConsoleUI.showSuccess("Cập nhật thông tin thành công!");
        } else {
            SimpleConsoleUI.showError("Cập nhật thông tin thất bại!");
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
    
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }
    
    private static boolean isValidPhoneNumber(String phone) {
        // Vietnamese phone number format
        String phoneRegex = "^(0[3-9][0-9]{8}|\\+84[3-9][0-9]{8})$";
        return phone.matches(phoneRegex);
    }
    
    private static String getRoleDisplay(String role) {
        switch (role) {
            case "ADMIN": return "Quản trị viên";
            case "EMPLOYEE": return "Nhân viên";
            case "SUPPORT_STAFF": return "Nhân viên hỗ trợ";
            default: return role;
        }
    }
    
    private static void viewAssignedMeetings() {
        SimpleConsoleUI.showHeader("DANH SÁCH CUỘC HỌP ĐƯỢC PHÂN CÔNG");
        
        List<Booking> assignedBookings = bookingService.getBookingsForSupportStaff(currentUser.getUserId());
        
        if (assignedBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Bạn chưa được phân công cuộc họp nào.");
            SimpleConsoleUI.showMessage("Chỉ các cuộc họp đã được duyệt mới được phân công cho nhân viên hỗ trợ.");
            return;
        }
        
        String[] headers = {"ID", "Tiêu đề", "Phòng", "Người đặt", "Bắt đầu", "Kết thúc", "Trạng thái", "Trạng thái chuẩn bị"};
        String[][] data = new String[assignedBookings.size()][8];
        
        for (int i = 0; i < assignedBookings.size(); i++) {
            Booking booking = assignedBookings.get(i);
            
            // Get room name
            var roomOpt = roomService.getRoomById(booking.getRoomId());
            String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
            
            // Get user name who booked
            var userOpt = userService.getUserById(booking.getUserId());
            String userName = userOpt.isPresent() ? userOpt.get().getFullName() : "Unknown";
            
            data[i][0] = String.valueOf(booking.getBookingId());
            data[i][1] = booking.getTitle();
            data[i][2] = roomName;
            data[i][3] = userName;
            data[i][4] = booking.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][5] = booking.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][6] = getStatusDisplay(booking.getStatus());
            data[i][7] = getPrepStatusDisplay(booking.getPrepStatus());
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
        
        // Show upcoming meetings that need preparation
        List<Booking> upcomingMeetings = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (Booking booking : assignedBookings) {
            if (booking.getStartTime().isAfter(now) && 
                "APPROVED".equals(booking.getStatus()) && 
                !"READY".equals(booking.getPrepStatus())) {
                upcomingMeetings.add(booking);
            }
        }
        
        if (!upcomingMeetings.isEmpty()) {
            SimpleConsoleUI.showMessage("CÁC CUỘC HỌP CẦN CHUẨN BỊ:");
            SimpleConsoleUI.showMessage("================================");
            for (Booking booking : upcomingMeetings) {
                var roomOpt = roomService.getRoomById(booking.getRoomId());
                String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
                
                SimpleConsoleUI.showMessage("ID: " + booking.getBookingId() + 
                    " - " + booking.getTitle() + 
                    " - Phòng: " + roomName + 
                    " - Thời gian: " + booking.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                    " - Trạng thái: " + getPrepStatusDisplay(booking.getPrepStatus()));
            }
            SimpleConsoleUI.showMessage("");
        }
    }
    
    private static void updateRoomPreparationStatus() {
        SimpleConsoleUI.showHeader("CẬP NHẬT TRẠNG THÁI CHUẨN BỊ PHÒNG");
        
        // Get assigned bookings
        List<Booking> assignedBookings = bookingService.getBookingsForSupportStaff(currentUser.getUserId());
        
        if (assignedBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Bạn chưa được phân công cuộc họp nào.");
            return;
        }
        
        // Filter for approved bookings that are not ready yet
        List<Booking> updatableBookings = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        
        for (Booking booking : assignedBookings) {
            if ("APPROVED".equals(booking.getStatus()) && 
                booking.getStartTime().isAfter(now)) {
                updatableBookings.add(booking);
            }
        }
        
        if (updatableBookings.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có cuộc họp nào cần cập nhật trạng thái chuẩn bị.");
            SimpleConsoleUI.showMessage("Chỉ các cuộc họp đã được duyệt và chưa diễn ra mới có thể cập nhật.");
            return;
        }
        
        // Show bookings that can be updated
        SimpleConsoleUI.showMessage("DANH SÁCH CUỘC HỌP CÓ THỂ CẬP NHẬT:");
        SimpleConsoleUI.showMessage("=====================================");
        
        String[] headers = {"ID", "Tiêu đề", "Phòng", "Bắt đầu", "Trạng thái hiện tại"};
        String[][] data = new String[updatableBookings.size()][5];
        
        for (int i = 0; i < updatableBookings.size(); i++) {
            Booking booking = updatableBookings.get(i);
            var roomOpt = roomService.getRoomById(booking.getRoomId());
            String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
            
            data[i][0] = String.valueOf(booking.getBookingId());
            data[i][1] = booking.getTitle();
            data[i][2] = roomName;
            data[i][3] = booking.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            data[i][4] = getPrepStatusDisplay(booking.getPrepStatus());
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
        
        try {
            int bookingId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID cuộc họp cần cập nhật: "));
            
            // Find the booking
            Booking selectedBooking = null;
            for (Booking booking : updatableBookings) {
                if (booking.getBookingId() == bookingId) {
                    selectedBooking = booking;
                    break;
                }
            }
            
            if (selectedBooking == null) {
                SimpleConsoleUI.showError("Không tìm thấy cuộc họp với ID này hoặc không thể cập nhật.");
                return;
            }
            
            // Show current status
            SimpleConsoleUI.showMessage("Trạng thái hiện tại: " + getPrepStatusDisplay(selectedBooking.getPrepStatus()));
            SimpleConsoleUI.showMessage("");
            
            // Show status options
            SimpleConsoleUI.showMessage("LỰA CHỌN TRẠNG THÁI MỚI:");
            SimpleConsoleUI.showMessage("1. CHƯA BẮT ĐẦU (NOT_STARTED)");
            SimpleConsoleUI.showMessage("2. ĐANG CHUẨN BỊ (PREPARING)");
            SimpleConsoleUI.showMessage("3. SẴN SÀNG (READY)");
            SimpleConsoleUI.showMessage("4. THIẾU THIẾT BỊ (MISSING_EQUIPMENT)");
            SimpleConsoleUI.showMessage("");
            
            int statusChoice = Integer.parseInt(SimpleConsoleUI.getInput("Chọn trạng thái mới (1-4): "));
            
            String newStatus;
            switch (statusChoice) {
                case 1:
                    newStatus = "NOT_STARTED";
                    break;
                case 2:
                    newStatus = "PREPARING";
                    break;
                case 3:
                    newStatus = "READY";
                    break;
                case 4:
                    newStatus = "MISSING_EQUIPMENT";
                    break;
                default:
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                    return;
            }
            
            // Confirm the update
            SimpleConsoleUI.showMessage("Xác nhận cập nhật trạng thái từ '" + 
                getPrepStatusDisplay(selectedBooking.getPrepStatus()) + 
                "' sang '" + getPrepStatusDisplay(newStatus) + "'?");
            String confirm = SimpleConsoleUI.getInput("Nhập 'yes' để xác nhận: ");
            
            if (!"yes".equalsIgnoreCase(confirm)) {
                SimpleConsoleUI.showMessage("Đã hủy cập nhật.");
                return;
            }
            
            // Update the booking
            boolean success = bookingService.updatePreparationStatus(bookingId, newStatus);
            
            if (success) {
                SimpleConsoleUI.showSuccess("Cập nhật trạng thái chuẩn bị thành công!");
                
                // Show updated information
                var updatedBooking = bookingService.getBookingById(bookingId);
                if (updatedBooking.isPresent()) {
                    SimpleConsoleUI.showMessage("Trạng thái mới: " + getPrepStatusDisplay(updatedBooking.get().getPrepStatus()));
                    
                    // If status is READY, notify that the room is ready
                    if ("READY".equals(newStatus)) {
                        SimpleConsoleUI.showMessage("");
                        SimpleConsoleUI.showSuccess("Phòng họp đã sẵn sàng cho cuộc họp!");
                        SimpleConsoleUI.showMessage("Nhân viên có thể bắt đầu cuộc họp.");
                    }
                    
                    // If status is MISSING_EQUIPMENT, suggest action
                    if ("MISSING_EQUIPMENT".equals(newStatus)) {
                        SimpleConsoleUI.showMessage("");
                        SimpleConsoleUI.showMessage("Vui lòng liên hệ quản trị viên để xử lý thiếu thiết bị.");
                    }
                }
            } else {
                SimpleConsoleUI.showError("Cập nhật trạng thái thất bại!");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Vui lòng nhập số hợp lệ!");
        }
    }
}

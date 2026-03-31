package com.prj.meeting.presentation;

import com.prj.meeting.model.User;
import com.prj.meeting.service.UserService;
import com.prj.meeting.util.PasswordHash;

import java.util.List;

public class UserManagementMenu {
    private static UserService userService = new UserService();
    
    public static void showMenu() {
        while (true) {
            SimpleConsoleUI.showHeader("QUẢN LÝ NGƯỜI DÙNG");
            
            String[] options = {
                "Xem danh sách người dùng",
                "Tìm kiếm người dùng",
                "Thêm người dùng",
                "Cập nhật thông tin người dùng",
                "Xóa người dùng",
                "Quay lại menu chính"
            };
            
            SimpleConsoleUI.showMenu("CHỌN CHỨC NĂNG", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        viewAllUsers();
                        break;
                    case 2:
                        searchUser();
                        break;
                    case 3:
                        addUser();
                        break;
                    case 4:
                        updateUser();
                        break;
                    case 5:
                        deleteUser();
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
    
    private static void viewAllUsers() {
        SimpleConsoleUI.showHeader("DANH SÁCH NGƯỜI DÙNG");
        
        List<User> users = userService.getAllUsers();
        
        if (users.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có người dùng nào trong hệ thống.");
            return;
        }
        
        String[] headers = {"ID", "Tên đăng nhập", "Họ tên", "Phòng ban", "Vai trò", "Email", "SĐT"};
        String[][] data = new String[users.size()][7];
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = String.valueOf(user.getUserId());
            data[i][1] = user.getUsername();
            data[i][2] = user.getFullName();
            data[i][3] = user.getDepartment();
            data[i][4] = getRoleDisplay(user.getRole());
            data[i][5] = user.getEmail();
            data[i][6] = user.getPhoneNumber();
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void searchUser() {
        SimpleConsoleUI.showHeader("TÌM KIẾM NGƯỜI DÙNG");
        
        String keyword = SimpleConsoleUI.getInput("Nhập từ khóa (tên đăng nhập, họ tên, email): ");
        
        List<User> allUsers = userService.getAllUsers();
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> 
                user.getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                user.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(keyword.toLowerCase())
            )
            .toList();
        
        if (filteredUsers.isEmpty()) {
            SimpleConsoleUI.showMessage("Không tìm thấy người dùng nào phù hợp.");
            return;
        }
        
        String[] headers = {"ID", "Tên đăng nhập", "Họ tên", "Phòng ban", "Vai trò", "Email", "SĐT"};
        String[][] data = new String[filteredUsers.size()][7];
        
        for (int i = 0; i < filteredUsers.size(); i++) {
            User user = filteredUsers.get(i);
            data[i][0] = String.valueOf(user.getUserId());
            data[i][1] = user.getUsername();
            data[i][2] = user.getFullName();
            data[i][3] = user.getDepartment();
            data[i][4] = getRoleDisplay(user.getRole());
            data[i][5] = user.getEmail();
            data[i][6] = user.getPhoneNumber();
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void addUser() {
        SimpleConsoleUI.showHeader("THÊM NGƯỜI DÙNG MỚI");
        
        String username = SimpleConsoleUI.getInput("Tên đăng nhập: ");
        if (username.isEmpty()) {
            SimpleConsoleUI.showError("Tên đăng nhập không được để trống!");
            return;
        }
        
        // Check for duplicate username
        if (userService.isUsernameExists(username)) {
            SimpleConsoleUI.showError("Tên đăng nhập đã tồn tại!");
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
        
        SimpleConsoleUI.showMessage("Chọn vai trò:");
        String[] roles = {"1. ADMIN", "2. EMPLOYEE", "3. SUPPORT_STAFF"};
        SimpleConsoleUI.showMenu("", roles);
        
        String role = "EMPLOYEE";
        try {
            int roleChoice = Integer.parseInt(SimpleConsoleUI.getInput(""));
            switch (roleChoice) {
                case 1: role = "ADMIN"; break;
                case 2: role = "EMPLOYEE"; break;
                case 3: role = "SUPPORT_STAFF"; break;
                default: SimpleConsoleUI.showError("Lựa chọn không hợp lệ, mặc định là EMPLOYEE");
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Lựa chọn không hợp lệ, mặc định là EMPLOYEE");
        }
        
        boolean success = userService.registerUser(username, password, role, fullName, department, phoneNumber, email);
        
        if (success) {
            SimpleConsoleUI.showSuccess("Thêm người dùng thành công!");
        } else {
            SimpleConsoleUI.showError("Thêm người dùng thất bại!");
        }
    }
    
    private static void updateUser() {
        SimpleConsoleUI.showHeader("CẬP NHẬT THÔNG TIN NGƯỜI DÙNG");
        
        try {
            int userId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID người dùng cần cập nhật: "));
            
            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                SimpleConsoleUI.showError("Không tìm thấy người dùng với ID: " + userId);
                return;
            }
            
            User user = userOpt.get();
            
            SimpleConsoleUI.showMessage("Thông tin hiện tại:");
            SimpleConsoleUI.showMessage("ID: " + user.getUserId());
            SimpleConsoleUI.showMessage("Tên đăng nhập: " + user.getUsername());
            SimpleConsoleUI.showMessage("Họ tên: " + user.getFullName());
            SimpleConsoleUI.showMessage("Phòng ban: " + user.getDepartment());
            SimpleConsoleUI.showMessage("Vai trò: " + getRoleDisplay(user.getRole()));
            SimpleConsoleUI.showMessage("Email: " + user.getEmail());
            SimpleConsoleUI.showMessage("SĐT: " + user.getPhoneNumber());
            SimpleConsoleUI.showMessage("");
            
            String newFullName = SimpleConsoleUI.getInput("Họ tên mới (để trống nếu không đổi): ");
            String newDepartment = SimpleConsoleUI.getInput("Phòng ban mới (để trống nếu không đổi): ");
            String newEmail = SimpleConsoleUI.getInput("Email mới (để trống nếu không đổi): ");
            String newPhoneNumber = SimpleConsoleUI.getInput("SĐT mới (để trống nếu không đổi): ");
            
            if (!newFullName.trim().isEmpty()) user.setFullName(newFullName.trim());
            if (!newDepartment.trim().isEmpty()) user.setDepartment(newDepartment.trim());
            if (!newEmail.trim().isEmpty()) user.setEmail(newEmail.trim());
            if (!newPhoneNumber.trim().isEmpty()) user.setPhoneNumber(newPhoneNumber.trim());
            
            SimpleConsoleUI.showMessage("Chọn vai trò mới (để trống nếu không đổi):");
            String[] roles = {"1. ADMIN", "2. EMPLOYEE", "3. SUPPORT_STAFF"};
            SimpleConsoleUI.showMenu("", roles);
            
            String roleInput = SimpleConsoleUI.getInput("");
            if (!roleInput.trim().isEmpty()) {
                try {
                    int roleChoice = Integer.parseInt(roleInput);
                    switch (roleChoice) {
                        case 1: user.setRole("ADMIN"); break;
                        case 2: user.setRole("EMPLOYEE"); break;
                        case 3: user.setRole("SUPPORT_STAFF"); break;
                        default: SimpleConsoleUI.showError("Lựa chọn không hợp lệ, giữ nguyên vai trò cũ");
                    }
                } catch (NumberFormatException e) {
                    SimpleConsoleUI.showError("Lựa chọn không hợp lệ, giữ nguyên vai trò cũ");
                }
            }
            
            boolean success = userService.updateUser(user);
            if (success) {
                SimpleConsoleUI.showSuccess("Cập nhật thông tin người dùng thành công!");
            } else {
                SimpleConsoleUI.showError("Cập nhật thông tin người dùng thất bại!");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
        }
    }
    
    private static void deleteUser() {
        SimpleConsoleUI.showHeader("XÓA NGƯỜI DÙNG");
        
        try {
            int userId = Integer.parseInt(SimpleConsoleUI.getInput("Nhập ID người dùng cần xóa: "));
            
            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                SimpleConsoleUI.showError("Không tìm thấy người dùng với ID: " + userId);
                return;
            }
            
            User user = userOpt.get();
            SimpleConsoleUI.showMessage("Thông tin người dùng:");
            SimpleConsoleUI.showMessage("ID: " + user.getUserId());
            SimpleConsoleUI.showMessage("Tên đăng nhập: " + user.getUsername());
            SimpleConsoleUI.showMessage("Họ tên: " + user.getFullName());
            SimpleConsoleUI.showMessage("");
            
            String confirm = SimpleConsoleUI.getInput("Bạn có chắc chắn muốn xóa người dùng này? (y/N): ");
            if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
                boolean success = userService.deleteUser(userId);
                if (success) {
                    SimpleConsoleUI.showSuccess("Xóa người dùng thành công!");
                } else {
                    SimpleConsoleUI.showError("Xóa người dùng thất bại!");
                }
            } else {
                SimpleConsoleUI.showMessage("Đã hủy thao tác xóa.");
            }
            
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("ID không hợp lệ!");
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
    
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }
    
    private static boolean isValidPhoneNumber(String phone) {
        // Vietnamese phone number format
        String phoneRegex = "^(0[3-9][0-9]{8}|\\+84[3-9][0-9]{8})$";
        return phone.matches(phoneRegex);
    }
}

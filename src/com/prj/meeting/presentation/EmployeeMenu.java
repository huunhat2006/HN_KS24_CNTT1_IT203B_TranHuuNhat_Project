package com.prj.meeting.presentation;

import com.prj.meeting.SimpleMainApplication;
import com.prj.meeting.model.User;
import com.prj.meeting.presentation.SimpleConsoleUI;

public class EmployeeMenu {
    
    public static void showMenu(User currentUser) {
        while (true) {
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
                        BookingMenu.showBookingMenu(currentUser);
                        break;
                    case 2:
                        BookingMenu.viewMyBookings(currentUser);
                        break;
                    case 3:
                        BookingMenu.cancelBooking(currentUser);
                        break;
                    case 4:
                        UserMenu.updateProfile(currentUser);
                        break;
                    case 5:
                        RoomMenu.viewAvailableRooms();
                        break;
                    case 6:
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
}

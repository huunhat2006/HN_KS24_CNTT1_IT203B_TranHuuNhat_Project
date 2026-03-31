package com.prj.meeting.presentation;

import com.prj.meeting.SimpleMainApplication;
import com.prj.meeting.model.User;
import com.prj.meeting.presentation.SimpleConsoleUI;

public class AdminMenu {
    
    public static void showMenu(User currentUser) {
        while (true) {
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
                        ReportMenu.showMenu();
                        break;
                    case 7:
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

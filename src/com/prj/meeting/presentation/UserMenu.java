package com.prj.meeting.presentation;

import com.prj.meeting.model.User;

public class UserMenu {
    
    public static void updateProfile(User currentUser) {
        ConsoleUI.showHeader("CẬP NHẬT THÔNG TIN CÁ NHÂN");
        
        System.out.println("Thông tin hiện tại:");
        System.out.println("Họ và tên: " + currentUser.getFullName());
        System.out.println("Phòng ban: " + currentUser.getDepartment());
        System.out.println("Số điện thoại: " + currentUser.getPhoneNumber());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println();
        
        String newFullName = ConsoleUI.getInput("Họ và tên mới (để trống nếu không đổi): ");
        String newDepartment = ConsoleUI.getInput("Phòng ban mới (để trống nếu không đổi): ");
        String newPhone = ConsoleUI.getInput("Số điện thoại mới (để trống nếu không đổi): ");
        String newEmail = ConsoleUI.getInput("Email mới (để trống nếu không đổi): ");
        
        if (!newFullName.isEmpty()) currentUser.setFullName(newFullName);
        if (!newDepartment.isEmpty()) currentUser.setDepartment(newDepartment);
        if (!newPhone.isEmpty()) currentUser.setPhoneNumber(newPhone);
        if (!newEmail.isEmpty()) currentUser.setEmail(newEmail);
        
        ConsoleUI.showSuccess("Cập nhật thông tin cá nhân thành công!");
    }
}

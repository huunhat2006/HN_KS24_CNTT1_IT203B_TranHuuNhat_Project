package com.prj.meeting.presentation;

import com.prj.meeting.service.EquipmentService;
import com.prj.meeting.model.Equipment;

import java.util.List;

public class EquipmentManagementMenu {
    
    private static EquipmentService equipmentService = new EquipmentService();
    
    public static void showMenu() {
        while (true) {
            String[] options = {
                "Xem danh sách thiết bị",
                "Thêm thiết bị mới",
                "Cập nhật thông tin thiết bị",
                "Xóa thiết bị",
                "Quay lại"
            };
            
            SimpleConsoleUI.showMenu("QUẢN LÝ THIẾT BỊ", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        viewEquipments();
                        break;
                    case 2:
                        addEquipment();
                        break;
                    case 3:
                        updateEquipment();
                        break;
                    case 4:
                        deleteEquipment();
                        break;
                    case 5:
                        return;
                    default:
                        SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                }
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Vui lòng nhập số!");
            }
        }
    }
    
    private static void viewEquipments() {
        SimpleConsoleUI.showHeader("DANH SÁCH THIẾT BỊ");
        
        List<Equipment> equipments = equipmentService.getAllEquipments();
        if (equipments.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có thiết bị nào.");
            return;
        }
        
        String[] headers = {"ID", "Tên thiết bị", "Tổng số", "Sẵn có", "Trạng thái"};
        String[][] data = new String[equipments.size()][5];
        
        for (int i = 0; i < equipments.size(); i++) {
            Equipment equipment = equipments.get(i);
            data[i][0] = String.valueOf(equipment.getEquipmentId());
            data[i][1] = equipment.getEquipmentName();
            data[i][2] = String.valueOf(equipment.getTotalQuantity());
            data[i][3] = String.valueOf(equipment.getAvailableQuantity());
            data[i][4] = getStatusText(equipment.getStatus());
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void addEquipment() {
        SimpleConsoleUI.showHeader("THÊM THIẾT BỊ MỚI");
        
        String name = SimpleConsoleUI.getInput("Tên thiết bị: ");
        if (name.isEmpty()) {
            SimpleConsoleUI.showError("Tên thiết bị không được để trống!");
            return;
        }
        
        // Check for duplicate equipment name
        if (equipmentService.isEquipmentNameExists(name)) {
            SimpleConsoleUI.showError("Tên thiết bị đã tồn tại!");
            return;
        }
        
        int totalQuantity = SimpleConsoleUI.getIntInput("Tổng số lượng: ");
        if (totalQuantity <= 0) {
            SimpleConsoleUI.showError("Tổng số lượng phải lớn hơn 0!");
            return;
        }
        
        int availableQuantity = SimpleConsoleUI.getIntInput("Số lượng sẵn có: ");
        if (availableQuantity < 0 || availableQuantity > totalQuantity) {
            SimpleConsoleUI.showError("Số lượng sẵn có không hợp lệ!");
            return;
        }
        
        SimpleConsoleUI.showMessage("Trạng thái:");
        SimpleConsoleUI.showMessage("1. Tốt");
        SimpleConsoleUI.showMessage("2. Hỏng");
        SimpleConsoleUI.showMessage("3. Bảo trì");
        int statusChoice = SimpleConsoleUI.getIntInput("Chọn trạng thái: ");
        
        String status = getStatusFromChoice(statusChoice);
        
        Equipment equipment = new Equipment(name, totalQuantity, availableQuantity, status);
        
        if (equipmentService.createEquipment(equipment)) {
            SimpleConsoleUI.showSuccess("Thêm thiết bị thành công!");
        } else {
            SimpleConsoleUI.showError("Thêm thiết bị thất bại!");
        }
    }
    
    private static void updateEquipment() {
        SimpleConsoleUI.showHeader("CẬP NHẬT THÔNG TIN THIẾT BỊ");
        
        int equipmentId = SimpleConsoleUI.getIntInput("Nhập ID thiết bị cần cập nhật: ");
        
        var equipmentOpt = equipmentService.getEquipmentById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy thiết bị với ID này!");
            return;
        }
        
        Equipment equipment = equipmentOpt.get();
        
        SimpleConsoleUI.showMessage("Thông tin hiện tại:");
        SimpleConsoleUI.showMessage("Tên thiết bị: " + equipment.getEquipmentName());
        SimpleConsoleUI.showMessage("Tổng số: " + equipment.getTotalQuantity());
        SimpleConsoleUI.showMessage("Sẵn có: " + equipment.getAvailableQuantity());
        SimpleConsoleUI.showMessage("Trạng thái: " + getStatusText(equipment.getStatus()));
        
        SimpleConsoleUI.showMessage("");
        SimpleConsoleUI.getInput("Nhấn Enter để tiếp tục...");
        
        String newName = SimpleConsoleUI.getInput("Tên thiết bị mới (để trống nếu không đổi): ");
        if (!newName.trim().isEmpty()) {
            equipment.setEquipmentName(newName);
        }
        
        String totalStr = SimpleConsoleUI.getInput("Tổng số mới (để trống nếu không đổi): ");
        if (!totalStr.trim().isEmpty()) {
            try {
                equipment.setTotalQuantity(Integer.parseInt(totalStr));
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Số lượng không hợp lệ!");
                return;
            }
        }
        
        String availableStr = SimpleConsoleUI.getInput("Số lượng sẵn có mới (để trống nếu không đổi): ");
        if (!availableStr.trim().isEmpty()) {
            try {
                equipment.setAvailableQuantity(Integer.parseInt(availableStr));
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Số lượng không hợp lệ!");
                return;
            }
        }
        
        SimpleConsoleUI.showMessage("Trạng thái mới:");
        SimpleConsoleUI.showMessage("1. Tốt");
        SimpleConsoleUI.showMessage("2. Hỏng");
        SimpleConsoleUI.showMessage("3. Bảo trì");
        String statusChoice = SimpleConsoleUI.getInput("Chọn trạng thái (để trống nếu không đổi): ");
        if (!statusChoice.trim().isEmpty()) {
            try {
                int choice = Integer.parseInt(statusChoice);
                equipment.setStatus(getStatusFromChoice(choice));
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                return;
            }
        }
        
        if (equipmentService.updateEquipment(equipment)) {
            SimpleConsoleUI.showSuccess("Cập nhật thông tin thiết bị thành công!");
        } else {
            SimpleConsoleUI.showError("Cập nhật thông tin thiết bị thất bại!");
        }
    }
    
    private static void deleteEquipment() {
        SimpleConsoleUI.showHeader("XÓA THIẾT BỊ");
        
        int equipmentId = SimpleConsoleUI.getIntInput("Nhập ID thiết bị cần xóa: ");
        
        var equipmentOpt = equipmentService.getEquipmentById(equipmentId);
        if (equipmentOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy thiết bị với ID này!");
            return;
        }
        
        Equipment equipment = equipmentOpt.get();
        SimpleConsoleUI.showMessage("Thông tin thiết bị:");
        SimpleConsoleUI.showMessage("Tên thiết bị: " + equipment.getEquipmentName());
        SimpleConsoleUI.showMessage("Tổng số: " + equipment.getTotalQuantity());
        
        if (SimpleConsoleUI.confirm("Bạn có chắc chắn muốn xóa thiết bị này?")) {
            if (equipmentService.deleteEquipment(equipmentId)) {
                SimpleConsoleUI.showSuccess("Xóa thiết bị thành công!");
            } else {
                SimpleConsoleUI.showError("Xóa thiết bị thất bại!");
            }
        } else {
            SimpleConsoleUI.showMessage("Đã hủy xóa thiết bị.");
        }
    }
    
    private static String getStatusText(String status) {
        switch (status) {
            case "GOOD": return "Tốt";
            case "BROKEN": return "Hỏng";
            case "MAINTENANCE": return "Bảo trì";
            default: return status;
        }
    }
    
    private static String getStatusFromChoice(int choice) {
        switch (choice) {
            case 2: return "BROKEN";
            case 3: return "MAINTENANCE";
            default: return "GOOD";
        }
    }
}

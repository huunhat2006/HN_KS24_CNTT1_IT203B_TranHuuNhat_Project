package com.prj.meeting.presentation;

import com.prj.meeting.service.ServiceService;
import com.prj.meeting.model.Service;

import java.util.List;

public class ServiceManagementMenu {
    
    private static ServiceService serviceService = new ServiceService();
    
    public static void showMenu() {
        while (true) {
            String[] options = {
                "Xem danh sách dịch vụ",
                "Thêm dịch vụ mới",
                "Cập nhật thông tin dịch vụ",
                "Xóa dịch vụ",
                "Quay lại"
            };
            
            SimpleConsoleUI.showMenu("QUẢN LÝ DỊCH VỤ", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        viewServices();
                        break;
                    case 2:
                        addService();
                        break;
                    case 3:
                        updateService();
                        break;
                    case 4:
                        deleteService();
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
    
    private static void viewServices() {
        SimpleConsoleUI.showHeader("DANH SÁCH DỊCH VỤ");
        
        List<Service> services = serviceService.getAllServices();
        if (services.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có dịch vụ nào.");
            return;
        }
        
        String[] headers = {"ID", "Tên dịch vụ", "Đơn giá", "Đơn vị"};
        String[][] data = new String[services.size()][4];
        
        for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            data[i][0] = String.valueOf(service.getServiceId());
            data[i][1] = service.getServiceName();
            data[i][2] = String.format("%,.0f VNĐ", service.getUnitPrice());
            data[i][3] = service.getUnit() != null ? service.getUnit() : "";
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void addService() {
        SimpleConsoleUI.showHeader("THÊM DỊCH VỤ MỚI");
        
        String name = SimpleConsoleUI.getInput("Tên dịch vụ: ");
        if (name.isEmpty()) {
            SimpleConsoleUI.showError("Tên dịch vụ không được để trống!");
            return;
        }
        
        // Check for duplicate service name
        if (serviceService.isServiceNameExists(name)) {
            SimpleConsoleUI.showError("Tên dịch vụ đã tồn tại!");
            return;
        }
        
        double unitPrice = 0;
        try {
            unitPrice = Double.parseDouble(SimpleConsoleUI.getInput("Đơn giá: "));
            if (unitPrice <= 0) {
                SimpleConsoleUI.showError("Đơn giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            SimpleConsoleUI.showError("Đơn giá không hợp lệ!");
            return;
        }
        
        String unit = SimpleConsoleUI.getInput("Đơn vị (ví dụ: ly, chai, phần, buổi): ");
        
        Service service = new Service(name, unitPrice, unit);
        
        if (serviceService.createService(service)) {
            SimpleConsoleUI.showSuccess("Thêm dịch vụ thành công!");
        } else {
            SimpleConsoleUI.showError("Thêm dịch vụ thất bại!");
        }
    }
    
    private static void updateService() {
        SimpleConsoleUI.showHeader("CẬP NHẬT THÔNG TIN DỊCH VỤ");
        
        int serviceId = SimpleConsoleUI.getIntInput("Nhập ID dịch vụ cần cập nhật: ");
        
        var serviceOpt = serviceService.getServiceById(serviceId);
        if (serviceOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy dịch vụ với ID này!");
            return;
        }
        
        Service service = serviceOpt.get();
        
        SimpleConsoleUI.showMessage("Thông tin hiện tại:");
        SimpleConsoleUI.showMessage("Tên dịch vụ: " + service.getServiceName());
        SimpleConsoleUI.showMessage("Đơn giá: " + String.format("%,.0f VNĐ", service.getUnitPrice()));
        SimpleConsoleUI.showMessage("Đơn vị: " + (service.getUnit() != null ? service.getUnit() : ""));
        
        SimpleConsoleUI.showMessage("");
        SimpleConsoleUI.getInput("Nhấn Enter để tiếp tục...");
        
        String newName = SimpleConsoleUI.getInput("Tên dịch vụ mới (để trống nếu không đổi): ");
        if (!newName.trim().isEmpty()) {
            service.setServiceName(newName);
        }
        
        String priceStr = SimpleConsoleUI.getInput("Đơn giá mới (để trống nếu không đổi): ");
        if (!priceStr.trim().isEmpty()) {
            try {
                service.setUnitPrice(Double.parseDouble(priceStr));
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Giá không hợp lệ!");
                return;
            }
        }
        
        String newUnit = SimpleConsoleUI.getInput("Đơn vị mới (để trống nếu không đổi): ");
        if (!newUnit.trim().isEmpty()) {
            service.setUnit(newUnit);
        }
        
        if (serviceService.updateService(service)) {
            SimpleConsoleUI.showSuccess("Cập nhật thông tin dịch vụ thành công!");
        } else {
            SimpleConsoleUI.showError("Cập nhật thông tin dịch vụ thất bại!");
        }
    }
    
    private static void deleteService() {
        SimpleConsoleUI.showHeader("XÓA DỊCH VỤ");
        
        int serviceId = SimpleConsoleUI.getIntInput("Nhập ID dịch vụ cần xóa: ");
        
        var serviceOpt = serviceService.getServiceById(serviceId);
        if (serviceOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy dịch vụ với ID này!");
            return;
        }
        
        Service service = serviceOpt.get();
        SimpleConsoleUI.showMessage("Thông tin dịch vụ:");
        SimpleConsoleUI.showMessage("Tên dịch vụ: " + service.getServiceName());
        SimpleConsoleUI.showMessage("Đơn giá: " + String.format("%,.0f VNĐ", service.getUnitPrice()));
        
        if (SimpleConsoleUI.confirm("Bạn có chắc chắn muốn xóa dịch vụ này?")) {
            if (serviceService.deleteService(serviceId)) {
                SimpleConsoleUI.showSuccess("Xóa dịch vụ thành công!");
            } else {
                SimpleConsoleUI.showError("Xóa dịch vụ thất bại!");
            }
        } else {
            SimpleConsoleUI.showMessage("Đã hủy xóa dịch vụ.");
        }
    }
}

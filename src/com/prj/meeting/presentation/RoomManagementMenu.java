package com.prj.meeting.presentation;

import com.prj.meeting.service.RoomService;
import com.prj.meeting.model.Room;

import java.util.List;

public class RoomManagementMenu {
    
    private static RoomService roomService = new RoomService();
    
    public static void showMenu() {
        while (true) {
            String[] options = {
                "Xem danh sách phòng họp",
                "Thêm phòng họp mới",
                "Cập nhật thông tin phòng",
                "Xóa phòng họp",
                "Tìm kiếm phòng họp",
                "Quay lại"
            };
            
            SimpleConsoleUI.showMenu("QUẢN LÝ PHÒNG HỌP", options);
            
            try {
                int choice = Integer.parseInt(SimpleConsoleUI.getInput(""));
                
                switch (choice) {
                    case 1:
                        viewRooms();
                        break;
                    case 2:
                        addRoom();
                        break;
                    case 3:
                        updateRoom();
                        break;
                    case 4:
                        deleteRoom();
                        break;
                    case 5:
                        searchRooms();
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
    
    private static void viewRooms() {
        SimpleConsoleUI.showHeader("DANH SÁCH PHÒNG HỌP");
        
        List<Room> rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            SimpleConsoleUI.showMessage("Không có phòng họp nào.");
            return;
        }
        
        String[] headers = {"ID", "Tên phòng", "Sức chứa", "Vị trí", "Thiết bị", "Trạng thái"};
        String[][] data = new String[rooms.size()][6];
        
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            data[i][0] = String.valueOf(room.getRoomId());
            data[i][1] = room.getRoomName();
            data[i][2] = String.valueOf(room.getCapacity());
            data[i][3] = room.getLocation();
            data[i][4] = room.getFixedEquipment();
            data[i][5] = room.getStatus().equals("ACTIVE") ? "Hoạt động" : "Bảo trì";
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
    
    private static void addRoom() {
        SimpleConsoleUI.showHeader("THÊM PHÒNG HỌP MỚI");
        
        String name = SimpleConsoleUI.getInput("Tên phòng: ");
        if (name.isEmpty()) {
            SimpleConsoleUI.showError("Tên phòng không được để trống!");
            return;
        }
        
        // Check for duplicate room name
        if (roomService.isRoomNameExists(name)) {
            SimpleConsoleUI.showError("Tên phòng đã tồn tại!");
            return;
        }
        
        int capacity = SimpleConsoleUI.getIntInput("Sức chứa: ");
        if (capacity <= 0) {
            SimpleConsoleUI.showError("Sức chứa phải lớn hơn 0!");
            return;
        }
        
        String location = SimpleConsoleUI.getInput("Vị trí: ");
        String equipment = SimpleConsoleUI.getInput("Thiết bị cố định: ");
        
        SimpleConsoleUI.showMessage("Trạng thái:");
        SimpleConsoleUI.showMessage("1. Hoạt động");
        SimpleConsoleUI.showMessage("2. Bảo trì");
        int statusChoice = SimpleConsoleUI.getIntInput("Chọn trạng thái: ");
        
        String status = (statusChoice == 2) ? "MAINTENANCE" : "ACTIVE";
        
        Room room = new Room(name, capacity, location, equipment, status);
        
        if (roomService.createRoom(room)) {
            SimpleConsoleUI.showSuccess("Thêm phòng họp thành công!");
        } else {
            SimpleConsoleUI.showError("Thêm phòng họp thất bại!");
        }
    }
    
    private static void updateRoom() {
        SimpleConsoleUI.showHeader("CẬP NHẬT THÔNG TIN PHÒNG");
        
        int roomId = SimpleConsoleUI.getIntInput("Nhập ID phòng cần cập nhật: ");
        
        var roomOpt = roomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy phòng với ID này!");
            return;
        }
        
        Room room = roomOpt.get();
        
        SimpleConsoleUI.showMessage("Thông tin hiện tại:");
        SimpleConsoleUI.showMessage("Tên phòng: " + room.getRoomName());
        SimpleConsoleUI.showMessage("Sức chứa: " + room.getCapacity());
        SimpleConsoleUI.showMessage("Vị trí: " + room.getLocation());
        SimpleConsoleUI.showMessage("Thiết bị: " + room.getFixedEquipment());
        SimpleConsoleUI.showMessage("Trạng thái: " + (room.getStatus().equals("ACTIVE") ? "Hoạt động" : "Bảo trì"));
        
        SimpleConsoleUI.showMessage("");
        SimpleConsoleUI.getInput("Nhấn Enter để tiếp tục...");
        
        String newName = SimpleConsoleUI.getInput("Tên phòng mới (để trống nếu không đổi): ");
        if (!newName.trim().isEmpty()) {
            room.setRoomName(newName);
        }
        
        String capacityStr = SimpleConsoleUI.getInput("Sức chứa mới (để trống nếu không đổi): ");
        if (!capacityStr.trim().isEmpty()) {
            try {
                room.setCapacity(Integer.parseInt(capacityStr));
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Sức chứa không hợp lệ!");
                return;
            }
        }
        
        String newLocation = SimpleConsoleUI.getInput("Vị trí mới (để trống nếu không đổi): ");
        if (!newLocation.trim().isEmpty()) {
            room.setLocation(newLocation);
        }
        
        String newEquipment = SimpleConsoleUI.getInput("Thiết bị mới (để trống nếu không đổi): ");
        if (!newEquipment.trim().isEmpty()) {
            room.setFixedEquipment(newEquipment);
        }
        
        SimpleConsoleUI.showMessage("Trạng thái mới:");
        SimpleConsoleUI.showMessage("1. Hoạt động");
        SimpleConsoleUI.showMessage("2. Bảo trì");
        String statusChoice = SimpleConsoleUI.getInput("Chọn trạng thái (để trống nếu không đổi): ");
        if (!statusChoice.trim().isEmpty()) {
            try {
                int choice = Integer.parseInt(statusChoice);
                room.setStatus((choice == 2) ? "MAINTENANCE" : "ACTIVE");
            } catch (NumberFormatException e) {
                SimpleConsoleUI.showError("Lựa chọn không hợp lệ!");
                return;
            }
        }
        
        if (roomService.updateRoom(room)) {
            SimpleConsoleUI.showSuccess("Cập nhật thông tin phòng thành công!");
        } else {
            SimpleConsoleUI.showError("Cập nhật thông tin phòng thất bại!");
        }
    }
    
    private static void deleteRoom() {
        SimpleConsoleUI.showHeader("XÓA PHÒNG HỌP");
        
        int roomId = SimpleConsoleUI.getIntInput("Nhập ID phòng cần xóa: ");
        
        var roomOpt = roomService.getRoomById(roomId);
        if (roomOpt.isEmpty()) {
            SimpleConsoleUI.showError("Không tìm thấy phòng với ID này!");
            return;
        }
        
        Room room = roomOpt.get();
        SimpleConsoleUI.showMessage("Thông tin phòng:");
        SimpleConsoleUI.showMessage("Tên phòng: " + room.getRoomName());
        SimpleConsoleUI.showMessage("Sức chứa: " + room.getCapacity());

        if (roomService.hasBookings(roomId)) {
            SimpleConsoleUI.showError("Không thể xóa phòng này vì đang có lịch đặt phòng!");
            SimpleConsoleUI.showMessage("Vui lòng hủy tất cả các lịch đặt phòng của phòng này trước khi xóa.");
            return;
        }
        
        if (SimpleConsoleUI.confirm("Bạn có chắc chắn muốn xóa phòng này?")) {
            if (roomService.deleteRoom(roomId)) {
                SimpleConsoleUI.showSuccess("Xóa phòng họp thành công!");
            } else {
                SimpleConsoleUI.showError("Xóa phòng họp thất bại! Có thể phòng đang được sử dụng trong các lịch đặt.");
            }
        } else {
            SimpleConsoleUI.showMessage("Đã hủy xóa phòng.");
        }
    }
    
    private static void searchRooms() {
        SimpleConsoleUI.showHeader("TÌM KIẾM PHÒNG HỌP");
        
        String searchTerm = SimpleConsoleUI.getInput("Nhập tên phòng cần tìm: ");
        if (searchTerm.isEmpty()) {
            SimpleConsoleUI.showError("Vui lòng nhập từ khóa tìm kiếm!");
            return;
        }
        
        List<Room> foundRooms = roomService.searchRoomsByName(searchTerm);
        
        if (foundRooms.isEmpty()) {
            SimpleConsoleUI.showMessage("Không tìm thấy phòng nào với từ khóa: " + searchTerm);
            return;
        }
        
        SimpleConsoleUI.showMessage("Tìm thấy " + foundRooms.size() + " phòng:");
        String[] headers = {"ID", "Tên phòng", "Sức chứa", "Vị trí", "Thiết bị", "Trạng thái"};
        String[][] data = new String[foundRooms.size()][6];
        
        for (int i = 0; i < foundRooms.size(); i++) {
            Room room = foundRooms.get(i);
            data[i][0] = String.valueOf(room.getRoomId());
            data[i][1] = room.getRoomName();
            data[i][2] = String.valueOf(room.getCapacity());
            data[i][3] = room.getLocation();
            data[i][4] = room.getFixedEquipment();
            data[i][5] = room.getStatus().equals("ACTIVE") ? "Hoạt động" : "Bảo trì";
        }
        
        SimpleConsoleUI.showTable(headers, data);
        SimpleConsoleUI.showMessage("");
    }
}

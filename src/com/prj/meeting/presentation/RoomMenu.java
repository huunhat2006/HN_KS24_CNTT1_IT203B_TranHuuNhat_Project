package com.prj.meeting.presentation;

import com.prj.meeting.model.Room;
import com.prj.meeting.service.RoomService;

import java.util.List;

public class RoomMenu {
    
    private static RoomService roomService = new RoomService();
    
    public static void viewAvailableRooms() {
        ConsoleUI.showHeader("DANH SÁCH PHÒNG TRỐNG");
        
        List<Room> rooms = roomService.getAvailableRooms();
        
        if (rooms.isEmpty()) {
            ConsoleUI.showMessage("Hiện tại không có phòng nào trống.");
            return;
        }

        String[] headers = {"ID", "Tên phòng", "Sức chứa", "Vị trí", "Thiết bị cố định"};
        String[][] data = new String[rooms.size()][5];
        
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            data[i][0] = String.valueOf(room.getRoomId());
            data[i][1] = room.getRoomName();
            data[i][2] = String.valueOf(room.getCapacity());
            data[i][3] = room.getLocation();
            data[i][4] = room.getFixedEquipment() != null ? room.getFixedEquipment() : "";
        }
        
        ConsoleUI.showTable(headers, data);
        ConsoleUI.showMessage("");
    }
}

package com.prj.meeting.presentation;

import com.prj.meeting.model.Booking;
import com.prj.meeting.model.Equipment;
import com.prj.meeting.model.Room;
import com.prj.meeting.model.Service;
import com.prj.meeting.model.User;
import com.prj.meeting.service.BookingService;
import com.prj.meeting.service.EquipmentService;
import com.prj.meeting.service.RoomService;
import com.prj.meeting.service.ServiceService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class BookingMenu {
    
    private static BookingService bookingService = new BookingService();
    private static RoomService roomService = new RoomService();
    private static EquipmentService equipmentService = new EquipmentService();
    private static ServiceService serviceService = new ServiceService();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void showBookingMenu(User currentUser) {
        ConsoleUI.showHeader("ĐẶT PHÒNG HỌP");
        
        try {
            // Step 1: Get time range
            ConsoleUI.showMessage("=== BƯỚC 1: CHỌN THỜI GIAN ===");
            
            String dateStr = ConsoleUI.getInput("Nhập ngày đặt phòng (yyyy-MM-dd): ");
            String startTimeStr = ConsoleUI.getInput("Nhập giờ bắt đầu (HH:mm): ");
            String endTimeStr = ConsoleUI.getInput("Nhập giờ kết thúc (HH:mm): ");
            
            LocalDateTime startTime = LocalDateTime.parse(dateStr + " " + startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endTime = LocalDateTime.parse(dateStr + " " + endTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            // Validate time
            if (startTime.isBefore(LocalDateTime.now())) {
                ConsoleUI.showError("Thời gian bắt đầu không thể là quá khứ!");
                return;
            }
            
            if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                ConsoleUI.showError("Thời gian kết thúc phải sau thời gian bắt đầu!");
                return;
            }
            
            // Step 2: Show available rooms
            ConsoleUI.showMessage("\n=== BƯỚC 2: DANH SÁCH PHÒNG TRỐNG ===");
            List<Room> availableRooms = roomService.getAvailableRoomsInTimeRange(startTime, endTime);
            
            if (availableRooms.isEmpty()) {
                ConsoleUI.showError("Không có phòng trống trong khung giờ đã chọn!");
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
                data[i][4] = room.getFixedEquipment() != null ? room.getFixedEquipment() : "Không có";
            }
            
            ConsoleUI.showTable(headers, data);
            
            // Step 3: Select room and get number of people
            int roomId = ConsoleUI.getIntInput("Nhập ID phòng muốn đặt: ");
            int numberOfPeople = ConsoleUI.getIntInput("Nhập số người tham gia: ");
            
            var roomOpt = roomService.getRoomById(roomId);
            if (roomOpt.isEmpty()) {
                ConsoleUI.showError("Không tìm thấy phòng với ID: " + roomId);
                return;
            }
            
            Room selectedRoom = roomOpt.get();
            
            // Validate capacity
            if (numberOfPeople > selectedRoom.getCapacity()) {
                ConsoleUI.showError("Số người (" + numberOfPeople + ") vượt quá sức chứa của phòng (" + selectedRoom.getCapacity() + ")!");
                return;
            }
            
            // Step 4: Select equipment
            ConsoleUI.showMessage("\n=== BƯỚC 3: CHỌN THIẾT BỊ (tùy chọn) ===");
            List<Equipment> availableEquipments = equipmentService.getAvailableEquipments();
            List<Integer> selectedEquipmentIds = new ArrayList<>();
            
            if (!availableEquipments.isEmpty()) {
                headers = new String[]{"ID", "Tên thiết bị", "Số lượng có sẵn", "Trạng thái"};
                data = new String[availableEquipments.size()][4];
                
                for (int i = 0; i < availableEquipments.size(); i++) {
                    Equipment equipment = availableEquipments.get(i);
                    data[i][0] = String.valueOf(equipment.getEquipmentId());
                    data[i][1] = equipment.getEquipmentName();
                    data[i][2] = String.valueOf(equipment.getAvailableQuantity());
                    data[i][3] = equipment.getStatus();
                }
                
                ConsoleUI.showTable(headers, data);
                
                ConsoleUI.showMessage("Nhập ID thiết bị muốn chọn (cách nhau bởi dấu phẩy, hoặc Enter để bỏ qua):");
                String equipmentInput = ConsoleUI.getInput("Thiết bị: ");
                
                if (!equipmentInput.trim().isEmpty()) {
                    String[] equipmentIds = equipmentInput.split(",");
                    for (String eqId : equipmentIds) {
                        try {
                            int id = Integer.parseInt(eqId.trim());
                            if (equipmentService.getEquipmentById(id).isPresent()) {
                                selectedEquipmentIds.add(id);
                            }
                        } catch (NumberFormatException e) {
                            ConsoleUI.showError("ID thiết bị không hợp lệ: " + eqId);
                        }
                    }
                }
            } else {
                ConsoleUI.showMessage("Không có thiết bị nào sẵn có.");
            }
            
            // Step 5: Select services
            ConsoleUI.showMessage("\n=== BƯỚC 4: CHỌN DỊCH VỤ (tùy chọn) ===");
            List<Service> availableServices = serviceService.getAllServices();
            List<Integer> selectedServiceIds = new ArrayList<>();
            
            if (!availableServices.isEmpty()) {
                headers = new String[]{"ID", "Tên dịch vụ", "Đơn giá", "Đơn vị"};
                data = new String[availableServices.size()][4];
                
                for (int i = 0; i < availableServices.size(); i++) {
                    Service service = availableServices.get(i);
                    data[i][0] = String.valueOf(service.getServiceId());
                    data[i][1] = service.getServiceName();
                    data[i][2] = String.valueOf(service.getUnitPrice());
                    data[i][3] = service.getUnit();
                }
                
                ConsoleUI.showTable(headers, data);
                
                ConsoleUI.showMessage("Nhập ID dịch vụ muốn chọn (cách nhau bởi dấu phẩy, hoặc Enter để bỏ qua):");
                String serviceInput = ConsoleUI.getInput("Dịch vụ: ");
                
                if (!serviceInput.trim().isEmpty()) {
                    String[] serviceIds = serviceInput.split(",");
                    for (String svId : serviceIds) {
                        try {
                            int id = Integer.parseInt(svId.trim());
                            if (serviceService.getServiceById(id).isPresent()) {
                                selectedServiceIds.add(id);
                            }
                        } catch (NumberFormatException e) {
                            ConsoleUI.showError("ID dịch vụ không hợp lệ: " + svId);
                        }
                    }
                }
            } else {
                ConsoleUI.showMessage("Không có dịch vụ nào.");
            }
            
            // Step 6: Get meeting details
            ConsoleUI.showMessage("\n=== BƯỚC 5: THÔNG TIN CUỘC HỌP ===");
            String title = ConsoleUI.getInput("Nhập tiêu đề cuộc họp: ");
            String description = ConsoleUI.getInput("Nhập mô tả cuộc họp: ");
            
            // Step 7: Confirm and create booking
            ConsoleUI.showMessage("\n=== XÁC NHẬN THÔNG TIN ===");
            ConsoleUI.showMessage("Phòng: " + selectedRoom.getRoomName());
            ConsoleUI.showMessage("Thời gian: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + 
                               " - " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ConsoleUI.showMessage("Số người: " + numberOfPeople);
            ConsoleUI.showMessage("Tiêu đề: " + title);
            ConsoleUI.showMessage("Mô tả: " + description);
            ConsoleUI.showMessage("Thiết bị: " + (selectedEquipmentIds.isEmpty() ? "Không" : selectedEquipmentIds.size() + " thiết bị"));
            ConsoleUI.showMessage("Dịch vụ: " + (selectedServiceIds.isEmpty() ? "Không" : selectedServiceIds.size() + " dịch vụ"));
            
            if (ConsoleUI.confirm("Xác nhận đặt phòng?")) {
                // Create booking
                Booking booking = new Booking(currentUser.getUserId(), roomId, title, description, startTime, endTime);
                
                boolean success = bookingService.createBookingWithEquipmentAndServices(
                    booking, selectedEquipmentIds, selectedServiceIds);
                
                if (success) {
                    ConsoleUI.showSuccess("Đặt phòng thành công! ID đặt phòng: " + booking.getBookingId());
                    ConsoleUI.showMessage("Trạng thái: PENDING (chờ duyệt)");
                } else {
                    ConsoleUI.showError("Đặt phòng thất bại! Phòng có thể đã được đặt trong khung giờ này.");
                }
            } else {
                ConsoleUI.showMessage("Đã hủy thao tác đặt phòng.");
            }
            
        } catch (Exception e) {
            ConsoleUI.showError("Lỗi: " + e.getMessage());
        }
    }
    
    public static void viewMyBookings(User currentUser) {
        ConsoleUI.showHeader("LỊCH HỌP CỦA TÔI");
        
        try {
            List<Booking> bookings = bookingService.getBookingsByUserId(currentUser.getUserId());
            
            if (bookings.isEmpty()) {
                ConsoleUI.showMessage("Bạn chưa có đặt phòng nào.");
                return;
            }
            
            String[] headers = {"ID", "Tiêu đề", "Phòng", "Bắt đầu", "Kết thúc", "Trạng thái", "Trạng thái chuẩn bị"};
            String[][] data = new String[bookings.size()][7];
            
            for (int i = 0; i < bookings.size(); i++) {
                Booking booking = bookings.get(i);
                var roomOpt = roomService.getRoomById(booking.getRoomId());
                String roomName = roomOpt.isPresent() ? roomOpt.get().getRoomName() : "Unknown";
                
                data[i][0] = String.valueOf(booking.getBookingId());
                data[i][1] = booking.getTitle();
                data[i][2] = roomName;
                data[i][3] = booking.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][4] = booking.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                data[i][5] = getStatusDisplay(booking.getStatus());
                data[i][6] = getPrepStatusDisplay(booking.getPrepStatus());
            }
            
            ConsoleUI.showTable(headers, data);
            ConsoleUI.showMessage("");
            
        } catch (Exception e) {
            ConsoleUI.showError("Lỗi: " + e.getMessage());
        }
    }
    
    private static String getStatusDisplay(String status) {
        switch (status) {
            case "PENDING": return "CHỜ DUYỆT";
            case "APPROVED": return "ĐÃ DUYỆT";
            case "REJECTED": return "BỊ TỪ CHỐI";
            case "CANCELLED": return "ĐÃ HỦY";
            default: return status;
        }
    }
    
    private static String getPrepStatusDisplay(String prepStatus) {
        switch (prepStatus) {
            case "NOT_STARTED": return "CHƯA BẮT ĐẦU";
            case "PREPARING": return "ĐANG CHUẨN BỊ";
            case "READY": return "SẴN SÀNG";
            case "MISSING_EQUIPMENT": return "THIẾU THIẾT BỊ";
            default: return prepStatus;
        }
    }
    
    public static void cancelBooking(User currentUser) {
        ConsoleUI.showHeader("HỦY LỊCH HỌP");
        
        try {
            int bookingId = ConsoleUI.getIntInput("Nhập ID lịch họp cần hủy: ");
            
            var bookingOpt = bookingService.getBookingById(bookingId);
            if (bookingOpt.isEmpty()) {
                ConsoleUI.showError("Không tìm thấy lịch họp với ID: " + bookingId);
                return;
            }
            
            Booking booking = bookingOpt.get();
            
            // Check if the booking belongs to current user
            if (booking.getUserId() != currentUser.getUserId()) {
                ConsoleUI.showError("Bạn không có quyền hủy lịch họp này!");
                return;
            }
            
            // Check if booking can be cancelled (only PENDING status)
            if (!"PENDING".equals(booking.getStatus())) {
                ConsoleUI.showError("Chỉ có thể hủy lịch họp khi đang ở trạng thái CHỜ DUYỆT!");
                ConsoleUI.showMessage("Trạng thái hiện tại: " + getStatusDisplay(booking.getStatus()));
                return;
            }
            
            ConsoleUI.showMessage("Thông tin lịch họp:");
            ConsoleUI.showMessage("Tiêu đề: " + booking.getTitle());
            var roomOpt = roomService.getRoomById(booking.getRoomId());
            if (roomOpt.isPresent()) {
                ConsoleUI.showMessage("Phòng: " + roomOpt.get().getRoomName());
            }
            ConsoleUI.showMessage("Thời gian: " + booking.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + 
                               " - " + booking.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ConsoleUI.showMessage("Trạng thái: " + getStatusDisplay(booking.getStatus()));
            ConsoleUI.showMessage("");
            
            if (ConsoleUI.confirm("Bạn có chắc chắn muốn hủy lịch họp này?")) {
                if (bookingService.cancelBooking(bookingId)) {
                    ConsoleUI.showSuccess("Hủy lịch họp thành công!");
                } else {
                    ConsoleUI.showError("Hủy lịch họp thất bại!");
                }
            } else {
                ConsoleUI.showMessage("Đã hủy thao tác.");
            }
            
        } catch (Exception e) {
            ConsoleUI.showError("Lỗi: " + e.getMessage());
        }
    }
}

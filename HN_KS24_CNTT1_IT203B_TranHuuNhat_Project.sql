CREATE DATABASE IF NOT EXISTS HN_KS24_CNTT1_IT203B_TranHuuNhat_Project;
USE HN_KS24_CNTT1_IT203B_TranHuuNhat_Project;

-- Drop existing tables
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS booking_services;
DROP TABLE IF EXISTS booking_equipments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS equipments;
DROP TABLE IF EXISTS rooms;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE', 'SUPPORT_STAFF') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create rooms table
CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    location VARCHAR(255),
    fixed_equipment TEXT,
    status ENUM('ACTIVE', 'MAINTENANCE') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create equipments table
CREATE TABLE equipments (
    equipment_id INT AUTO_INCREMENT PRIMARY KEY,
    equipment_name VARCHAR(100) NOT NULL,
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    status ENUM('GOOD', 'BROKEN', 'MAINTENANCE') DEFAULT 'GOOD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create services table
CREATE TABLE services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    room_id INT NOT NULL,
    support_staff_id INT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    prep_status ENUM('NOT_STARTED', 'PREPARING', 'READY', 'MISSING_EQUIPMENT') DEFAULT 'NOT_STARTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (support_staff_id) REFERENCES users(user_id)
);

-- Create booking_equipments table
CREATE TABLE booking_equipments (
    booking_id INT NOT NULL,
    equipment_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (booking_id, equipment_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (equipment_id) REFERENCES equipments(equipment_id)
);

-- Create booking_services table
CREATE TABLE booking_services (
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (booking_id, service_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (service_id) REFERENCES services(service_id)
);

-- Create reviews table
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

-- Insert sample data

-- Insert users with hashed passwords
INSERT INTO users (username, password_hash, role, full_name, department, phone_number, email) VALUES 
('admin', '77dMRGT75BTm1oUz2aJloD3qo/AnUhGNSDftOcMxfNRBaU/jWuVf5zNV6Fmea1UE', 'ADMIN', 'Quản trị viên', 'IT', '0123456789', 'admin@congty.com'),
('hn', '77dMRGT75BTm1oUz2aJloD3qo/AnUhGNSDftOcMxfNRBaU/jWuVf5zNV6Fmea1UE', 'EMPLOYEE', 'Trần Hữu Nhật', 'Kinh doanh', '0912345678', 'thn@congty.com'),
('ht', '77dMRGT75BTm1oUz2aJloD3qo/AnUhGNSDftOcMxfNRBaU/jWuVf5zNV6Fmea1UE', 'SUPPORT_STAFF', 'NVA', 'Hỗ trợ kỹ thuật', '0934567890', 'nva@congty.com');

-- Insert rooms
INSERT INTO rooms (room_name, capacity, location, fixed_equipment, status) VALUES 
('Phòng họp A', 10, 'Tầng 1', 'Máy chiếu, Bảng trắng, Điều hòa', 'ACTIVE'),
('Phòng họp B', 20, 'Tầng 2', 'Máy chiếu, Loa, Bảng thông minh, Điều hòa', 'ACTIVE'),
('Phòng họp C', 15, 'Tầng 1', 'Máy chiếu, Điều hòa', 'ACTIVE'),
('Phòng họp D', 8, 'Tầng 3', 'Bảng trắng, Điều hòa', 'ACTIVE'),
('Phòng hội nghị lớn', 50, 'Tầng 4', 'Máy chiếu, Loa, Bảng thông minh, Điều hòa, Hệ thống họp trực tuyến', 'ACTIVE');

-- Insert equipments
INSERT INTO equipments (equipment_name, total_quantity, available_quantity, status) VALUES 
('Máy chiếu', 5, 4, 'GOOD'),
('Loa di động', 10, 8, 'GOOD'),
('Microphone không dây', 8, 6, 'GOOD'),
('Laptop trình chiếu', 3, 2, 'GOOD'),
('Bảng di động', 5, 5, 'GOOD'),
('Máy in', 2, 2, 'GOOD');

-- Insert services
INSERT INTO services (service_name, unit_price, unit) VALUES 
('Trà đá', 5000, 'ly'),
('Cà phê', 15000, 'ly'),
('Nước suối', 8000, 'chai'),
('Bánh ngọt', 25000, 'phần'),
('Trái cây', 35000, 'đĩa'),
('Setup thiết bị', 100000, 'buổi');

-- Insert sample bookings
INSERT INTO bookings (user_id, room_id, title, description, start_time, end_time, status, prep_status) VALUES 
(2, 1, 'Họp team Sales', 'Họp kế hoạch bán hàng tháng 4', '2024-04-01 09:00:00', '2024-04-01 11:00:00', 'APPROVED', 'READY'),
(3, 2, 'Training nhân viên mới', 'Đào tạo kiến thức công ty', '2024-04-02 14:00:00', '2024-04-02 17:00:00', 'APPROVED', 'PREPARING'),
(2, 3, 'Họp với khách hàng', 'Thảo luận hợp đồng dự án ABC', '2024-04-03 10:00:00', '2024-04-03 12:00:00', 'PENDING', 'NOT_STARTED');

-- Insert booking_equipments
INSERT INTO booking_equipments (booking_id, equipment_id, quantity) VALUES 
(1, 1, 1), -- Booking 1 needs 1 projector
(1, 3, 2), -- Booking 1 needs 2 microphones
(2, 1, 1), -- Booking 2 needs 1 projector
(2, 4, 1), -- Booking 2 needs 1 laptop
(3, 1, 1), -- Booking 3 needs 1 projector
(3, 2, 2); -- Booking 3 needs 2 speakers

-- Insert booking_services
INSERT INTO booking_services (booking_id, service_id, quantity) VALUES 
(1, 2, 10), -- Booking 1 needs 10 coffees
(1, 3, 5),  -- Booking 1 needs 5 water bottles
(2, 1, 15), -- Booking 2 needs 15 iced teas
(2, 4, 10), -- Booking 2 needs 10 pastries
(3, 2, 5),  -- Booking 3 needs 5 coffees
(3, 6, 1);  -- Booking 3 needs equipment setup

-- Insert reviews
INSERT INTO reviews (booking_id, rating, comment) VALUES 
(1, 5, 'Phòng họp tốt, thiết bị đầy đủ, dịch vụ chuyên nghiệp.'),
(2, 4, 'Phòng rộng rãi, nhưng máy chiếu hơi tối.');

-- Update equipment quantities after bookings
UPDATE equipments SET available_quantity = total_quantity - (
    SELECT COALESCE(SUM(quantity), 0) 
    FROM booking_equipments 
    WHERE equipment_id = equipments.equipment_id 
    AND booking_id IN (SELECT booking_id FROM bookings WHERE status != 'CANCELLED')
);

COMMIT;

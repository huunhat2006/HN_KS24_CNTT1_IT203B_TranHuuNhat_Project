package com.prj.meeting.dao.impl;

import com.prj.meeting.dao.EquipmentDAO;
import com.prj.meeting.model.Equipment;
import com.prj.meeting.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipmentDAOImpl implements EquipmentDAO {
    
    @Override
    public Optional<Equipment> findById(int id) {
        String sql = "SELECT * FROM equipments WHERE equipment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    @Override
    public List<Equipment> findAll() {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT * FROM equipments";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                equipments.add(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipments;
    }
    
    @Override
    public List<Equipment> findByStatus(String status) {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT * FROM equipments WHERE status = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                equipments.add(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipments;
    }
    
    @Override
    public boolean save(Equipment equipment) {
        String sql = "INSERT INTO equipments (equipment_name, total_quantity, available_quantity, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, equipment.getEquipmentName());
            stmt.setInt(2, equipment.getTotalQuantity());
            stmt.setInt(3, equipment.getAvailableQuantity());
            stmt.setString(4, equipment.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    equipment.setEquipmentId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean update(Equipment equipment) {
        String sql = "UPDATE equipments SET equipment_name = ?, total_quantity = ?, available_quantity = ?, status = ? WHERE equipment_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipment.getEquipmentName());
            stmt.setInt(2, equipment.getTotalQuantity());
            stmt.setInt(3, equipment.getAvailableQuantity());
            stmt.setString(4, equipment.getStatus());
            stmt.setInt(5, equipment.getEquipmentId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM equipments WHERE equipment_id = ?";
        
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        Equipment equipment = new Equipment();
        equipment.setEquipmentId(rs.getInt("equipment_id"));
        equipment.setEquipmentName(rs.getString("equipment_name"));
        equipment.setTotalQuantity(rs.getInt("total_quantity"));
        equipment.setAvailableQuantity(rs.getInt("available_quantity"));
        equipment.setStatus(rs.getString("status"));
        return equipment;
    }
}

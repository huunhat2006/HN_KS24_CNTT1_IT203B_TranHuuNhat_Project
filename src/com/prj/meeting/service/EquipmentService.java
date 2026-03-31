package com.prj.meeting.service;

import com.prj.meeting.dao.EquipmentDAO;
import com.prj.meeting.dao.impl.EquipmentDAOImpl;
import com.prj.meeting.model.Equipment;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class EquipmentService {
    private final EquipmentDAO equipmentDAO;
    
    public EquipmentService() {
        this.equipmentDAO = new EquipmentDAOImpl();
    }
    
    public List<Equipment> getAllEquipments() {
        return equipmentDAO.findAll();
    }
    
    public Optional<Equipment> getEquipmentById(int equipmentId) {
        return equipmentDAO.findById(equipmentId);
    }
    
    public boolean createEquipment(Equipment equipment) {
        return equipmentDAO.save(equipment);
    }
    
    public boolean updateEquipment(Equipment equipment) {
        return equipmentDAO.update(equipment);
    }
    
    public boolean deleteEquipment(int equipmentId) {
        return equipmentDAO.delete(equipmentId);
    }
    
    public List<Equipment> getAvailableEquipments() {
        return equipmentDAO.findByStatus("GOOD");
    }
    
    public boolean isEquipmentNameExists(String equipmentName) {
        List<Equipment> allEquipments = equipmentDAO.findAll();
        for (Equipment equipment : allEquipments) {
            if (equipment.getEquipmentName().equalsIgnoreCase(equipmentName)) {
                return true;
            }
        }
        return false;
    }
}

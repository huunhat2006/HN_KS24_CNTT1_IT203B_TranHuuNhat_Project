package com.prj.meeting.dao;

import com.prj.meeting.model.Equipment;
import java.util.List;
import java.util.Optional;

public interface EquipmentDAO {
    Optional<Equipment> findById(int id);
    List<Equipment> findAll();
    List<Equipment> findByStatus(String status);
    boolean save(Equipment equipment);
    boolean update(Equipment equipment);
    boolean delete(int id);
}

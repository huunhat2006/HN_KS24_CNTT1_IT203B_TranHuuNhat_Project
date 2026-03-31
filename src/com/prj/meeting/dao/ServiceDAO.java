package com.prj.meeting.dao;

import com.prj.meeting.model.Service;
import java.util.List;
import java.util.Optional;

public interface ServiceDAO {
    Optional<Service> findById(int id);
    List<Service> findAll();
    boolean save(Service service);
    boolean update(Service service);
    boolean delete(int id);
}

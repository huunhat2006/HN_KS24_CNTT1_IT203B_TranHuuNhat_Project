package com.prj.meeting.service;

import com.prj.meeting.dao.ServiceDAO;
import com.prj.meeting.dao.impl.ServiceDAOImpl;
import com.prj.meeting.model.Service;

import java.util.List;
import java.util.Optional;

public class ServiceService {
    private final ServiceDAO serviceDAO;
    
    public ServiceService() {
        this.serviceDAO = new ServiceDAOImpl();
    }
    
    public List<Service> getAllServices() {
        return serviceDAO.findAll();
    }
    
    public Optional<Service> getServiceById(int serviceId) {
        return serviceDAO.findById(serviceId);
    }
    
    public boolean createService(Service service) {
        return serviceDAO.save(service);
    }
    
    public boolean updateService(Service service) {
        return serviceDAO.update(service);
    }
    
    public boolean deleteService(int serviceId) {
        return serviceDAO.delete(serviceId);
    }
    
    public boolean isServiceNameExists(String serviceName) {
        List<Service> allServices = serviceDAO.findAll();
        for (Service service : allServices) {
            if (service.getServiceName().equalsIgnoreCase(serviceName)) {
                return true;
            }
        }
        return false;
    }
}

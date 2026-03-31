package com.prj.meeting.model;

import java.math.BigDecimal;

public class Service {
    private int serviceId;
    private String serviceName;
    private BigDecimal unitPrice;
    private String unit;
    
    public Service() {}
    
    public Service(String serviceName, BigDecimal unitPrice, String unit) {
        this.serviceName = serviceName;
        this.unitPrice = unitPrice;
        this.unit = unit;
    }
    
    public Service(String serviceName, double unitPrice, String unit) {
        this.serviceName = serviceName;
        this.unitPrice = BigDecimal.valueOf(unitPrice);
        this.unit = unit;
    }
    
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public void setUnitPrice(double unitPrice) { this.unitPrice = BigDecimal.valueOf(unitPrice); }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", unitPrice=" + unitPrice +
                ", unit='" + unit + '\'' +
                '}';
    }
}

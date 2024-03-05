package com.example.toysocialnetwork.utils;

public class AdminEvent implements Event{

    private ServiceType serviceType;

    public AdminEvent(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
}

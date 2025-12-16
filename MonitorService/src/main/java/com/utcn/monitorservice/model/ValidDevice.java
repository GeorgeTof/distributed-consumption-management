package com.utcn.monitorservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "valid_devices")
public class ValidDevice {

    @Id
    private Long deviceId;

    @Column(name = "max_consumption")
    private Double maxConsumption;

    @Column(name = "owner_username")
    private String ownerUsername;

    public ValidDevice() {}

    public ValidDevice(Long deviceId, Double maxConsumption, String ownerUsername) {
        this.deviceId = deviceId;
        this.maxConsumption = maxConsumption;
        this.ownerUsername = ownerUsername;
    }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public Double getMaxConsumption() { return maxConsumption; }
    public void setMaxConsumption(Double maxConsumption) { this.maxConsumption = maxConsumption; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }
}
package com.utcn.monitorservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "valid_devices")
public class ValidDevice {

    @Id
    private Long deviceId;

    public ValidDevice() {}

    public ValidDevice(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }
}
package com.utcn.deviceservice.dto.builders;

import com.utcn.deviceservice.dto.DeviceDTO;
import com.utcn.deviceservice.model.Device;

public class DeviceBuilder {

    public static DeviceDTO toDTO(Device device) {
        return new DeviceDTO(
                device.getId(),
                device.getName(),
                device.getOwnerUsername(),
                device.getBrand(),
                device.getMaximumConsumption(),
                device.getPowerConsumed()
        );
    }

    public static Device toEntity(DeviceDTO deviceDTO) {
        return new Device(
                deviceDTO.name(),
                deviceDTO.ownerUsername(),
                deviceDTO.brand(),
                deviceDTO.maximumConsumption()
        );
    }
}
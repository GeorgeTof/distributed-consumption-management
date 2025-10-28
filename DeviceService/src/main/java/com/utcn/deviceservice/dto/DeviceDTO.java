package com.utcn.deviceservice.dto;

public record DeviceDTO(
        Long id,
        String name,
        String ownerUsername,
        String brand,
        Double maximumConsumption,
        Double powerConsumed
) {

}
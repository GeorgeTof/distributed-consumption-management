package com.utcn.deviceservice.dto;

public record DeviceDTO(
        Long id,
        String name,
        String brand,
        Double maximumConsumption
) {

}
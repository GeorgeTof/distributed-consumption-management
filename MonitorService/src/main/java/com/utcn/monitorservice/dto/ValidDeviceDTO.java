package com.utcn.monitorservice.dto;

public record ValidDeviceDTO(
        Long deviceId,
        Double maxConsumption,
        String ownerUsername
) {}
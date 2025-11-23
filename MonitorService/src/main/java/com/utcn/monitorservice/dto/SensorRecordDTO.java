package com.utcn.monitorservice.dto;

public record SensorRecordDTO(
        Long id,
        Long deviceId,
        Double measurement,
        int year,
        int month,
        int day,
        int hour
) {}
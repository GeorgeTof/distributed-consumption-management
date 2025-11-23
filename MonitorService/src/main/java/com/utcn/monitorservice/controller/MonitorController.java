package com.utcn.monitorservice.controller;

import com.utcn.monitorservice.dto.SensorRecordDTO;
import com.utcn.monitorservice.model.SensorRecord;
import com.utcn.monitorservice.repo.SensorRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MonitorController {

    private final SensorRecordRepository repository;

    public MonitorController(SensorRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/all")
    public List<SensorRecordDTO> getAllMeasurements() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/device/{id}")
    public List<SensorRecordDTO> getByDevice(@PathVariable Long id) {
        return repository.findByDeviceId(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SensorRecordDTO convertToDTO(SensorRecord entity) {
        return new SensorRecordDTO(
                entity.getId(),
                entity.getDeviceId(),
                entity.getMeasurement(),
                entity.getYear(),
                entity.getMonth(),
                entity.getDay(),
                entity.getHour()
        );
    }
}
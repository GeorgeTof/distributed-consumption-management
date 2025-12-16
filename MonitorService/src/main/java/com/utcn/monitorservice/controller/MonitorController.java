package com.utcn.monitorservice.controller;

import com.utcn.monitorservice.dto.SensorRecordDTO;
import com.utcn.monitorservice.dto.ValidDeviceDTO;
import com.utcn.monitorservice.model.SensorRecord;
import com.utcn.monitorservice.model.ValidDevice;
import com.utcn.monitorservice.repo.SensorRecordRepository;
import com.utcn.monitorservice.repo.ValidDeviceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    private final SensorRecordRepository repository;
    private final ValidDeviceRepository validDeviceRepository;

    public MonitorController(SensorRecordRepository repository, ValidDeviceRepository validDeviceRepository) {
        this.repository = repository;
        this.validDeviceRepository = validDeviceRepository;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SensorRecordDTO> getAllMeasurements() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/device/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SensorRecordDTO> getByDevice(@PathVariable Long id) {
        return repository.findByDeviceId(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public List<SensorRecordDTO> getDeviceHistory(
            @RequestParam Long deviceId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {

        return repository.findByDeviceIdAndYearAndMonthAndDay(deviceId, year, month, day)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/valid-devices")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Long> getAllValidDeviceIds() {
        return validDeviceRepository.findAll().stream()
                .map(ValidDevice::getDeviceId)
                .collect(Collectors.toList());
    }

    @GetMapping("/valid-devices-full")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ValidDeviceDTO> getAllValidDevicesFull() {
        return validDeviceRepository.findAll().stream()
                .map(device -> new ValidDeviceDTO(
                        device.getDeviceId(),
                        device.getMaxConsumption(),
                        device.getOwnerUsername()))
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
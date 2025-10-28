package com.utcn.deviceservice.service;

import com.utcn.deviceservice.dto.DeviceDTO;
import com.utcn.deviceservice.dto.builders.DeviceBuilder;
import com.utcn.deviceservice.model.Device;
import com.utcn.deviceservice.repo.DeviceRepository;
import com.utcn.deviceservice.handlers.exceptions.model.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();

        return deviceList.stream()
                .map(DeviceBuilder::toDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO findDeviceById(Long id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);

        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        return DeviceBuilder.toDTO(deviceOptional.get());
    }

    public Long insert(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);

        if (device.getOwnerUsername() == null || device.getOwnerUsername().isBlank()) {
            throw new RuntimeException("ownerUsername must be provided by admin");
        }

        device.setPowerConsumed(0.0);

        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public List<DeviceDTO> findDeviceByUsername(String username) {
        List<Device> deviceList = deviceRepository.findByOwnerUsername(username);

        return deviceList.stream()
                .map(DeviceBuilder::toDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO updateDeviceConsumption(Long id, Double powerConsumed) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device with id: " + id));

        device.setPowerConsumed(powerConsumed);
        Device updatedDevice = deviceRepository.save(device);

        return DeviceBuilder.toDTO(updatedDevice);
    }

    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException("Device with id: " + id);
        }

        deviceRepository.deleteById(id);
        LOGGER.debug("Device with id {} was deleted from db", id);
    }
}
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

        device = deviceRepository.save(device);

        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }
}
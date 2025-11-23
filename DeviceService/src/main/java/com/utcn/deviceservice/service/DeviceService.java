package com.utcn.deviceservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.deviceservice.config.RabbitConfig;
import com.utcn.deviceservice.dto.DeviceDTO;
import com.utcn.deviceservice.dto.builders.DeviceBuilder;
import com.utcn.deviceservice.handlers.exceptions.model.BadRequestException;
import com.utcn.deviceservice.handlers.exceptions.model.ResourceNotFoundException;
import com.utcn.deviceservice.model.Device;
import com.utcn.deviceservice.repo.DeviceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DeviceService(DeviceRepository deviceRepository, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.deviceRepository = deviceRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
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
        validateDeviceForCreation(deviceDTO);

        Device device = DeviceBuilder.toEntity(deviceDTO);
        device.setPowerConsumed(0.0);

        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());

        try {
            Map<String, Object> eventMessage = new HashMap<>();
            eventMessage.put("deviceId", device.getId());
//            eventMessage.put("userUsername", device.getOwnerUsername());

            String jsonPayload = objectMapper.writeValueAsString(eventMessage);

            rabbitTemplate.convertAndSend(RabbitConfig.DEVICE_EVENTS_QUEUE, jsonPayload);
            LOGGER.info("Published Device Created Event for ID: {}", device.getId());

        } catch (Exception e) {
            LOGGER.error("Failed to send Device Created event to RabbitMQ", e);
        }

        return device.getId();
    }

    public List<DeviceDTO> findDeviceByUsername(String username) {
        List<Device> deviceList = deviceRepository.findByOwnerUsername(username);

        return deviceList.stream()
                .map(DeviceBuilder::toDTO)
                .collect(Collectors.toList());
    }

    public DeviceDTO updateDeviceConsumption(Long id, Double powerConsumed) {
        if (powerConsumed == null || powerConsumed < 0.0) {
            throw new BadRequestException("Power consumption cannot be null or negative.");
        }

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

//    @Transactional
    public void deleteDevicesByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new BadRequestException("Username must not be empty.");
        }

        deviceRepository.deleteByOwnerUsername(username);
        LOGGER.debug("All devices for user {} were deleted from db", username);
    }

    private void validateDeviceForCreation(DeviceDTO dto) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("Device name must not be empty.");
        }
        if (dto.ownerUsername() == null || dto.ownerUsername().isBlank()) {
            throw new BadRequestException("ownerUsername must be provided.");
        }
        if (dto.brand() == null || dto.brand().isBlank()) {
            throw new BadRequestException("Device brand must not be empty.");
        }
        if (dto.maximumConsumption() == null || dto.maximumConsumption() <= 0.0) {
            throw new BadRequestException("Maximum consumption must be a positive number.");
        }
    }
}
package com.utcn.monitorservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.monitorservice.model.ValidDevice;
import com.utcn.monitorservice.repo.SensorRecordRepository;
import com.utcn.monitorservice.repo.ValidDeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeviceEventConsumer {

    private final ValidDeviceRepository validDeviceRepository;
    private final SensorRecordRepository sensorRecordRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeviceEventConsumer(ValidDeviceRepository repository, SensorRecordRepository sensorRecordRepository) {
        this.validDeviceRepository = repository;
        this.sensorRecordRepository = sensorRecordRepository;
    }

    @Transactional
    @RabbitListener(queues = "${internal.queue.name}", containerFactory = "internalContainerFactory")
    public void receiveDeviceEvent(String jsonMessage) {
        try {
            System.out.println("Received Device Event: " + jsonMessage);

            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);
            String eventType = (String) message.get("eventType");

            Object deviceIdObj = message.get("deviceId");
            Long deviceId = Long.parseLong(deviceIdObj.toString());

            if ("DEVICE_CREATED".equals(eventType)) {
                ValidDevice validDevice = new ValidDevice(deviceId);
                validDeviceRepository.save(validDevice);
                System.out.println(">>> Synced: Added Device ID " + deviceId);

            } else if ("DEVICE_DELETED".equals(eventType)) {
                if (validDeviceRepository.existsById(deviceId)) {
                    validDeviceRepository.deleteById(deviceId);
                    System.out.println(">>> Synced: Removed Device ID " + deviceId);
                    sensorRecordRepository.deleteByDeviceId(deviceId);
                    System.out.println(">>> Synced: Deleted all sensor measurements for Device ID " + deviceId);
                } else {
                    System.out.println(">>> Synced: Ignored Delete for unknown Device ID " + deviceId);
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing device event: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
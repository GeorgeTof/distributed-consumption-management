package com.utcn.monitorservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.monitorservice.model.ValidDevice;
import com.utcn.monitorservice.repo.SensorRecordRepository;
import com.utcn.monitorservice.repo.ValidDeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.util.Map;

@Service
@ConditionalOnExpression("'${INSTANCE_INDEX:0}' == '0'")
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
                Object maxConsObj = message.get("maxConsumption");
                Double maxConsumption = (maxConsObj != null) ? Double.parseDouble(maxConsObj.toString()) : 0.0;
                String ownerUsername = (String) message.get("userId");

                ValidDevice validDevice = new ValidDevice(deviceId, maxConsumption, ownerUsername);

                validDeviceRepository.save(validDevice);
                System.out.println(">>> Synced: Added Device ID " + deviceId + " [User: " + ownerUsername + ", Max: " + maxConsumption + "]");

            } else if ("DEVICE_DELETED".equals(eventType)) {
                if (validDeviceRepository.existsById(deviceId)) {
                    validDeviceRepository.deleteById(deviceId);
                    System.out.println(">>> Synced: Removed Device ID " + deviceId);
                    sensorRecordRepository.deleteByDeviceId(deviceId);
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing device event: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
package com.utcn.monitorservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.monitorservice.model.ValidDevice;
import com.utcn.monitorservice.repo.ValidDeviceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeviceEventConsumer {

    private final ValidDeviceRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DeviceEventConsumer(ValidDeviceRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${internal.queue.name}", containerFactory = "internalContainerFactory")
    public void receiveDeviceEvent(String jsonMessage) {
        try {
            System.out.println("Received Device Event: " + jsonMessage);

            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);

            Object deviceIdObj = message.get("deviceId");
            Long deviceId = Long.parseLong(deviceIdObj.toString());

            ValidDevice validDevice = new ValidDevice(deviceId);
            repository.save(validDevice);

            System.out.println(">>> Synced New Device ID to Monitor DB: " + deviceId);

        } catch (Exception e) {
            System.err.println("Error processing device event: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
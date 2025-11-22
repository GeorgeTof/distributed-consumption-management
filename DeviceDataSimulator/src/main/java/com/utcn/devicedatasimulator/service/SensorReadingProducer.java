package com.utcn.devicedatasimulator.service;

import com.utcn.devicedatasimulator.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SensorReadingProducer {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 1. Hardcoded list of Device IDs (Replace these with UUIDs from your DB later)
    private final List<String> deviceIds = Arrays.asList(   // TODO update to real devices
            "ID-1111",
            "ID-2222",
            "ID-3333"
    );

    private LocalDateTime currentSimulatedTime = LocalDateTime.now();

    public SensorReadingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void sendSimulationData() {
        try {
            currentSimulatedTime = currentSimulatedTime.plusMinutes(10);
            String timestampStr = currentSimulatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            for (String deviceId : deviceIds) {
                double measurement = 20 + random.nextDouble() * 10;

                Map<String, Object> data = new HashMap<>();
                data.put("timestamp", timestampStr);
                data.put("device_id", deviceId);
                data.put("measurement_value", measurement);

                String jsonString = objectMapper.writeValueAsString(data);

                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, jsonString);

                System.out.println("Sent for " + deviceId + ": " + timestampStr + " | " + measurement);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
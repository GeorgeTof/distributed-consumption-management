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

    /*
    6 - lawnmower   - 70
    7 - Iphone      - 34
     */

    private final List<String> deviceIds = Arrays.asList(   // TODO update to real devices
            "1111",
            "3",
            "6",
            "7"
    );

    private final List<Integer> maxConsumptions = Arrays.asList(
            60,
            5,
            70,
            34
    );

    private final List<Integer> heavyHours = Arrays.asList(
            6,
            7,
            17,
            18,
            19
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

            for (int i = 0; i < deviceIds.size(); i++) {
                String deviceId = deviceIds.get(i);
                double scaleFactorByHour = heavyHours.contains(currentSimulatedTime.getHour()) ? 1.0 : 0.7;
                double measurement = random.nextDouble() * maxConsumptions.get(i) * scaleFactorByHour;
                measurement = Math.round(measurement * 10000.0) / 10000.0;

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
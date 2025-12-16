package com.utcn.devicedatasimulator.service;

import com.utcn.devicedatasimulator.config.RabbitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${device.index:0}")
    private int deviceIndex;

    private final List<String> deviceIds = Arrays.asList(
            "1111", // invalid
            "3",    // invalid
            "11",
            "12"
    );

    private final List<Integer> maxConsumptions = Arrays.asList(
            60,
            5,
            30,
            15
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
            if (deviceIndex < 0 || deviceIndex >= deviceIds.size()) {
                System.err.println("ERROR: Invalid device index " + deviceIndex + ". Must be between 0 and " + (deviceIds.size() - 1));
                return;
            }

            currentSimulatedTime = currentSimulatedTime.plusMinutes(10);
            String timestampStr = currentSimulatedTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String deviceId = deviceIds.get(deviceIndex);
            double maxCons = maxConsumptions.get(deviceIndex);

            double scaleFactorByHour = heavyHours.contains(currentSimulatedTime.getHour()) ? 1.0 : 0.7;
            double measurement = random.nextDouble() * maxCons * scaleFactorByHour;
            measurement = Math.round(measurement * 10000.0) / 10000.0;

            Map<String, Object> data = new HashMap<>();
            data.put("timestamp", timestampStr);
            data.put("device_id", deviceId);
            data.put("measurement_value", measurement);

            String jsonString = objectMapper.writeValueAsString(data);

            rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, jsonString);

            System.out.println("Sent for " + deviceId + " (Index " + deviceIndex + "): " + timestampStr + " | " + measurement);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
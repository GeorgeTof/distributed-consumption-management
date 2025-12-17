package com.utcn.monitorservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.monitorservice.config.RabbitConfig; // Ensure this import exists
import com.utcn.monitorservice.model.SensorRecord;
import com.utcn.monitorservice.model.ValidDevice;
import com.utcn.monitorservice.repo.SensorRecordRepository;
import com.utcn.monitorservice.repo.ValidDeviceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorConsumer {

    @Value("${INSTANCE_INDEX:0}")
    private String instanceIndex;

    private final SensorRecordRepository repository;
    private final ValidDeviceRepository validDeviceRepository;
    private final RabbitTemplate internalRabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<Long, List<Double>> deviceBuffer = new ConcurrentHashMap<>();

    public SensorConsumer(SensorRecordRepository repository,
                          ValidDeviceRepository validDeviceRepository,
                          @Qualifier("internalRabbitTemplate") RabbitTemplate internalRabbitTemplate) {
        this.repository = repository;
        this.validDeviceRepository = validDeviceRepository;
        this.internalRabbitTemplate = internalRabbitTemplate;
    }

    @RabbitListener(queues = "${app.queue.name}", containerFactory = "sensorContainerFactory")
    public void receiveMessage(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);

            Object deviceIdObj = message.get("device_id");
            Long deviceId = Long.parseLong(deviceIdObj.toString());

            ValidDevice device = validDeviceRepository.findById(deviceId).orElse(null);

            if (device == null) {
                System.out.print("#" + instanceIndex + " ");
                System.out.println("Received data for unknown Device ID: " + deviceId + ". Discarding.");
                return;
            }

            Object measurementObj = message.get("measurement_value");
            Double measurement = Double.parseDouble(measurementObj.toString());
            String timestampStr = (String) message.get("timestamp");

            Double maxAllowed = device.getMaxConsumption();

            if (maxAllowed != null && measurement > maxAllowed) {
                System.err.println("!!! ALERT: Device " + deviceId + " exceeded limit! Val: " + measurement + ", Max: " + maxAllowed);

                Map<String, Object> alert = new HashMap<>();
                alert.put("device_id", deviceId);
                alert.put("measurement_value", measurement);
                alert.put("max_consumption", maxAllowed);
                alert.put("username", device.getOwnerUsername());
                alert.put("timestamp", timestampStr);

                internalRabbitTemplate.convertAndSend(RabbitConfig.OVERCONSUMPTION_QUEUE, objectMapper.writeValueAsString(alert));
            }

            deviceBuffer.putIfAbsent(deviceId, new ArrayList<>());
            List<Double> buffer = deviceBuffer.get(deviceId);

            synchronized (buffer) {
                buffer.add(measurement);

                System.out.print("#" + instanceIndex + " ");
                System.out.println("Buffer for Device " + deviceId + ": " + buffer.size() + "/6");

                if (buffer.size() >= 6) {
                    double totalHourlyConsumption = buffer.stream().mapToDouble(Double::doubleValue).sum();

                    SensorRecord record = new SensorRecord();
                    record.setDeviceId(deviceId);
                    record.setMeasurement(totalHourlyConsumption);

                    LocalDateTime dt = LocalDateTime.parse(timestampStr, formatter);
                    record.setYear(dt.getYear());
                    record.setMonth(dt.getMonthValue());
                    record.setDay(dt.getDayOfMonth());
                    record.setHour(dt.getHour());

                    repository.save(record);
                    System.out.println(">>> SAVED HOURLY AGGREGATE for Device " + deviceId + ": " + totalHourlyConsumption);

                    buffer.clear();
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing message: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
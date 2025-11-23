package com.utcn.monitorservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.monitorservice.model.SensorRecord;
import com.utcn.monitorservice.repo.SensorRecordRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class SensorConsumer {

    private final SensorRecordRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Formatter matching your Simulator's output
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SensorConsumer(SensorRecordRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "${app.queue.name}")
    public void receiveMessage(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);

            SensorRecord record = new SensorRecord();

            Object deviceIdObj = message.get("device_id");
            record.setDeviceId(Long.parseLong(deviceIdObj.toString()));

            Object measurementObj = message.get("measurement_value");
            record.setMeasurement(Double.parseDouble(measurementObj.toString()));

            String timestampStr = (String) message.get("timestamp");
            LocalDateTime dt = LocalDateTime.parse(timestampStr, formatter);

            record.setYear(dt.getYear());
            record.setMonth(dt.getMonthValue());
            record.setDay(dt.getDayOfMonth());
            record.setHour(dt.getHour());

            repository.save(record);
            System.out.println("Saved: Device " + record.getDeviceId() + " at " + record.getHour() + ":00");

        } catch (Exception e) {
            System.err.println("Error processing message: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
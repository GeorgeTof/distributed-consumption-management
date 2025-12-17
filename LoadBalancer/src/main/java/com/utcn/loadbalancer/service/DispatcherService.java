package com.utcn.loadbalancer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utcn.loadbalancer.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DispatcherService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${monitor.instances.count:1}")
    private int monitorInstancesCount;

    public DispatcherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.INPUT_QUEUE)
    public void dispatch(String jsonMessage) {
        try {
            Map<String, Object> message = objectMapper.readValue(jsonMessage, Map.class);

            Object deviceIdObj = message.get("device_id");
            if (deviceIdObj == null) {
                System.err.println("Error: Message missing device_id");
                return;
            }

            long deviceId = Long.parseLong(deviceIdObj.toString());

            long targetIndex = deviceId % monitorInstancesCount;

            String targetQueue = RabbitConfig.OUTPUT_QUEUE_PREFIX + targetIndex;

            rabbitTemplate.convertAndSend(targetQueue, jsonMessage);

            System.out.println("Forwarded Device " + deviceId + " -> " + targetQueue);

        } catch (Exception e) {
            System.err.println("Failed to dispatch message: " + jsonMessage);
            e.printStackTrace();
        }
    }
}
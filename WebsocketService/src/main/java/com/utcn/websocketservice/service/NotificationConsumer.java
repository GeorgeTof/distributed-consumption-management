package com.utcn.websocketservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationConsumer {

    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationConsumer(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RabbitListener(queues = "${app.queue.name}")
    public void receiveMessage(String message) {
        try {
            Map<String, Object> payload = objectMapper.readValue(message, Map.class);
            String username = (String) payload.get("username");

            if (username != null) {
                String destination = "/topic/client/" + username;

                System.out.println(">>> ROUTING ALERT TO: " + destination);

                this.template.convertAndSend(destination, message);
            } else {
                System.err.println("Received alert without username, cannot route: " + message);
            }

        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
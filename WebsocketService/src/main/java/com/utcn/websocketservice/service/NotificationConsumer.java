package com.utcn.websocketservice.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @RabbitListener(queues = "${app.queue.name}")
    public void receiveMessage(String message) {
        System.out.println(">>> WEBSOCKET SERVICE RECEIVED ALERT: " + message);
        // TODO: parse this JSON and push to WebSocket
    }
}
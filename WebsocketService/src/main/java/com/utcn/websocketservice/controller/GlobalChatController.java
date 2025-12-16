package com.utcn.websocketservice.controller;

import com.utcn.websocketservice.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GlobalChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessage broadcastMessage(@Payload ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(String.valueOf(System.currentTimeMillis()));
        }

        System.out.println("Broadcasting global message from: " + message.getSender());

        return message;
    }
}
package com.utcn.websocketservice.controller;

import com.utcn.websocketservice.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SupportChatController {

    private final SimpMessagingTemplate template;

    public SupportChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/support")
    public void handleSupportMessage(@Payload ChatMessage message) {
        System.out.println("Received support message from: " + message.getSender());

        String responseText = "I did not understand that.";

        String content = message.getContent().toLowerCase();

        if (content.contains("total") && content.contains("consumption")) {
            responseText = "To view your consumption, navigate to the Dashboard and click 'Show My Energy Consumption'.";
        } else if (content.contains("device")) {
            responseText = "You can view your associated devices in the 'My Devices' list on the dashboard.";
        }

        ChatMessage response = new ChatMessage("Support Bot", responseText);
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));

        String destination = "/topic/support/" + message.getSender();
        this.template.convertAndSend(destination, response);
    }
}
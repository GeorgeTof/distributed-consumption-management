package com.utcn.websocketservice.controller;

import com.utcn.websocketservice.dto.ChatMessage;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SupportChatController {

    private final SimpMessagingTemplate template;

    private static final String SYSTEM_CONTEXT =
            "You are a helpful support assistant for a Distributed Energy Management System. " +
            "Keep your answers short (max 2 sentences), friendly, and helpful. " +
            "If the user asks something irrelevant to energy, device management, or the app, politely refuse. " +
            "Context: ";

    public SupportChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/support")
    public void handleSupportMessage(@Payload ChatMessage message) {
        System.out.println("Received support message from: " + message.getSender());

        String content = message.getContent().toLowerCase();
        String responseText;

        if (content.contains("total") && content.contains("consumption")) {
            responseText = "To view your consumption, navigate to the Dashboard and click 'Show My Energy Consumption'.";
        }
        else if (content.contains("total") && content.contains("devices")) {
            responseText = "You can view your associated devices in the 'My Devices' list on the dashboard.";
        }
        else if (((content.contains("specific") || content.contains("individual")) && content.contains("device"))
                && content.contains("consumption")) {
            responseText = "To view a specific device's consumption, find the device card in your list and click the specific 'Consumption' button on that card (do not use the main button).";
        }
        else if (content.contains("change") && content.contains("password")) {
            responseText = "Changing your password is not currently supported. Please keep your credentials safe.";
        }
        else if (content.contains("delete") && content.contains("device")) {
            responseText = "Only administrators can delete devices. Please contact an admin.";
        }
        else if (content.contains("overconsumption")) {
            responseText = "If overconsumption occurs, you will receive an automatic warning alert. Please contact an administrator immediately if this happens repeatedly.";
        }
        else if (content.contains("delete") && content.contains("account")) {
            responseText = "To delete your account, please contact an administrator.";
        }
        else if (content.contains("view") && content.contains("today")) {
            responseText = "To view today's data, click 'Show My Energy Consumption' and simply select today's date in the calendar popup.";
        }
        else if (content.contains("update") && content.contains("email")) {
            responseText = "To update your email address, please contact an administrator.";
        }
        else if (content.contains("pay") || content.contains("payment") || content.contains("subscription")) {
            responseText = "Our application is free to use, but donations are appreciated!";
        }
        else {
            responseText = callGeminiSdk(message.getContent());
        }

        // --- SEND RESPONSE ---
        ChatMessage response = new ChatMessage("Support Bot", responseText, "SUPPORT");
        response.setTimestamp(String.valueOf(System.currentTimeMillis()));

        String destination = "/topic/support/" + message.getSender();
        this.template.convertAndSend(destination, response);
    }

    private String callGeminiSdk(String userQuestion) {
        try {
            Client client = new Client();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            SYSTEM_CONTEXT + userQuestion,
                            null);

            return response.text();

        } catch (Exception e) {
            System.err.println("Gemini SDK Error: " + e.getMessage());
            e.printStackTrace();
            return "I am having trouble connecting to the AI brain right now. Please try again later.";
        }
    }
}
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
            "You are the dedicated AI Support Assistant for the 'Distributed Energy Management System'. " +
            "Your role is to guide users through the application functionalities and explain its limitations. " +
            "Start by answering the user's question directly. " +
            "GUIDELINES: Keep answers short (max 3 sentences), friendly, and professional. " +
            "If the user asks about topics unrelated to energy, devices, or this app, politely refuse. " +

            "APP CAPABILITIES & NAVIGATION: " +
            "- Dashboard: Users can view their profile and a list of their owned smart devices. " +
            "- Global Consumption: To see total energy usage, click the main 'Show My Energy Consumption' button at the top of the dashboard and select a date. " +
            "- Device Consumption: To see history for just ONE device, do NOT use the main button. Instead, find the specific Device Card in the list and click the 'Consumption' button inside that card. " +
            "- Charts: Data is displayed as an hourly bar chart (00:00 to 23:00) in Watts. " +
            "- Alerts: The system monitors sensors in real-time. If a device exceeds its defined limit, an automated alert will pop up instantly. " +
            "- Chat: There is a 'Community Chat' button (green) to talk with other users globally. " +

            "APP LIMITATIONS (CRITICAL): " +
            "- Registration: Users CANNOT sign up themselves. Only an Administrator can create new accounts. " +
            "- editing: Users CANNOT change their passwords, update emails, or delete devices/accounts. " +
            "- Action Required: For any account modification, password change, or deletion request, tell the user they MUST contact an Administrator. " +
            "- Payments: The application is completely free to use. " +

            "User Question: ";

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
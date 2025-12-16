package com.utcn.websocketservice.dto;

public class ChatMessage {
    private String sender;
    private String content;
    private String role; // New field
    private String timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String content, String role) {
        this.sender = sender;
        this.content = content;
        this.role = role;
    }

    // Getters and Setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
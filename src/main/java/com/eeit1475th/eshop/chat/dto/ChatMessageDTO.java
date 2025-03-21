package com.eeit1475th.eshop.chat.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessageDTO {
    private String userMessage;
    private String botResponse;
    private String timestamp;
    private String userAvatar;
    private String botAvatar;

    public ChatMessageDTO(String userMessage, String botResponse, String timestamp, String userAvatar, String botAvatar) {
        this.userMessage = userMessage;
        this.botResponse = botResponse;
        this.timestamp = timestamp;
        this.userAvatar = userAvatar;
        this.botAvatar = botAvatar;
    }

    public ChatMessageDTO(String userMessage, String botResponse) {
        this(
            userMessage,
            botResponse,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "/images/user-avatar.png",
            "/images/bot-avatar.png"
        );
    }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public String getBotResponse() { return botResponse; }
    public void setBotResponse(String botResponse) { this.botResponse = botResponse; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getBotAvatar() { return botAvatar; }
    public void setBotAvatar(String botAvatar) { this.botAvatar = botAvatar; }
}

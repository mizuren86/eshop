package com.eeit1475th.eshop.chat.dto;

import lombok.Data;

@Data
public class CustomerMessage {
    private String content;
    private String senderType; // "customer" 或 "agent"
    private long timestamp;

    public CustomerMessage() {}

    public CustomerMessage(String content, String senderType) {
        this(content, senderType, System.currentTimeMillis());
    }

    public CustomerMessage(String content, String senderType, long timestamp) {
        this.content = content;
        this.senderType = senderType;
        this.timestamp = timestamp;
    }
}
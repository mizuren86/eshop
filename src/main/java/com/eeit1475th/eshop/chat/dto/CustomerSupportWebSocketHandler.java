package com.eeit1475th.eshop.chat.dto;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CustomerSupportWebSocketHandler extends TextWebSocketHandler {

    // 日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(CustomerSupportWebSocketHandler.class);

    // 用來標識當前客服模式，true 表示人工客服，false 表示智能客服
    private boolean isHumanMode = false;

    // 獲取當前的客服模式
    public boolean isHumanMode() {
        return isHumanMode;
    }

    // 設置客服模式
    public void setIsHumanMode(boolean isHumanMode) {
        this.isHumanMode = isHumanMode;
        logger.info("客服模式已切換為: {}", isHumanMode ? "人工客服" : "智能客服");
    }

    // 接收客戶端訊息
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String incomingMessage = message.getPayload();
        logger.info("收到客戶端訊息: {}", incomingMessage);

        if (isHumanMode) {
            // 處理人工客服模式下的訊息
            handleHumanMessage(session, incomingMessage);
        } else {
            // 處理智能客服模式下的訊息 (例如，透過 RASA API 或其他 AI 處理邏輯)
            handleAIMessage(session, incomingMessage);
        }
    }

    // 處理人工客服訊息
    private void handleHumanMessage(WebSocketSession session, String message) throws Exception {
        // 假設這裡會有實際的人工客服處理邏輯
        String response = "人工客服: " + message;
        logger.info("回應人工客服訊息: {}", response);
        session.sendMessage(new TextMessage(response));
    }

    // 處理智能客服訊息
    private void handleAIMessage(WebSocketSession session, String message) throws Exception {
        // 假設這裡是智能客服的邏輯，例如將訊息發送到 AI 系統或 API
        String response = "智能客服: " + message;
        logger.info("回應智能客服訊息: {}", response);
        session.sendMessage(new TextMessage(response));
    }

    // 切換人工客服模式
    public void toggleHumanMode() {
        this.isHumanMode = !this.isHumanMode;
        logger.info("客服模式切換為: {}", isHumanMode ? "人工客服" : "智能客服");
    }
}

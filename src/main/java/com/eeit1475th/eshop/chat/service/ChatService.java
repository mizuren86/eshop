package com.eeit1475th.eshop.chat.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eeit1475th.eshop.chat.dto.ChatMessageDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final List<ChatMessageDTO> chatHistory = new ArrayList<>();

    @Value("${rasa.api.url}")
    private String rasaApiUrl;

    private static final String USER_AVATAR = "/images/user.png"; // 使用者頭像
    private static final String BOT_AVATAR = "/images/bot.png";  // 客服頭像

    public List<ChatMessageDTO> getChatHistory() {
        return chatHistory;
    }

    public ChatMessageDTO processUserMessage(String userMessage) {
        // 設定時間戳記
        String timestamp = getCurrentTimestamp();

        // 若聊天記錄為空，則顯示歡迎訊息
        if (chatHistory.isEmpty()) {
            ChatMessageDTO welcomeMessage = new ChatMessageDTO("", "🛎️ 客服：您好！我是數位客服，請問有什麼可以幫助您的呢？", timestamp, "", BOT_AVATAR);
            chatHistory.add(welcomeMessage);
        }

        // 產生使用者的訊息
        ChatMessageDTO userMessageDto = new ChatMessageDTO("🧑‍💬 " + userMessage, "", timestamp, USER_AVATAR, "");
        chatHistory.add(userMessageDto);

        // 發送訊息給 Rasa 並獲取回應
        ChatMessageDTO botResponse = sendMessageToRasa(userMessage, timestamp);
        chatHistory.add(botResponse);

        return botResponse;
    }

    private ChatMessageDTO sendMessageToRasa(String userMessage, String timestamp) {
        RestTemplate restTemplate = new RestTemplate();
        String sender = "user";

        // 構建 Rasa 請求 JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("sender", sender);
        requestBody.put("message", userMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(rasaApiUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            return new ChatMessageDTO("", "⚠️ 客服：目前系統忙碌中，請稍後再試！", timestamp, "", BOT_AVATAR);
        }

        // 解析 Rasa 回應
        String rasaResponse = parseRasaResponse(response.getBody());

        return new ChatMessageDTO("", "🛎️ 客服：" + rasaResponse, timestamp, "", BOT_AVATAR);
    }

    private String parseRasaResponse(String responseBody) {
        JSONArray jsonArray = new JSONArray(responseBody);
        StringBuilder responseText = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject message = jsonArray.getJSONObject(i);
            if (message.has("text")) {
                responseText.append(message.getString("text")).append("\n");
            }
        }

        return responseText.toString().trim();
    }

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}

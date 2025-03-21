package com.eeit1475th.eshop.chat.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eeit1475th.eshop.chat.dto.ChatMessageDTO;

import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private final List<ChatMessageDTO> chatHistory = new ArrayList<>();
    
    @Value("${rasa.api.url}")
    private String rasaApiUrl;

    public List<ChatMessageDTO> getChatHistory() {
        return chatHistory;
    }

    public ChatMessageDTO processUserMessage(String userMessage) {
        if (chatHistory.isEmpty()) {
            ChatMessageDTO welcomeMessage = new ChatMessageDTO("", "🛎️ 客服：您好！我是數位客服，請問有什麼可以幫助您的呢？");
            chatHistory.add(welcomeMessage);
        }

        ChatMessageDTO chatMessage = sendMessageToRasa(userMessage);
        chatHistory.add(chatMessage);

        return chatMessage;
    }

    private ChatMessageDTO sendMessageToRasa(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();
        String sender = "user";

        JSONObject requestBody = new JSONObject();
        requestBody.put("sender", sender);
        requestBody.put("message", userMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                rasaApiUrl, HttpMethod.POST, entity, String.class);

        String rasaResponse = parseRasaResponse(response.getBody());

        return new ChatMessageDTO("🧑‍💬 " + userMessage, "🛎️ 客服：" + rasaResponse);
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
}


package com.eeit1475th.eshop.controller;

import com.eeit1475th.eshop.chat.dto.ChatMessageDTO;
import com.eeit1475th.eshop.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody MessageRequest messageRequest) {
        String userMessage = messageRequest.getMessage();
        ChatMessageDTO chatMessage = chatService.processUserMessage(userMessage);
        return ResponseEntity.ok(chatMessage);
    }

    // Simple DTO for message request
    public static class MessageRequest {
        private String message;

        // Getters and Setters
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

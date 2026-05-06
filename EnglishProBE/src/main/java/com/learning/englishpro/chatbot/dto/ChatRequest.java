package com.learning.englishpro.chatbot.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private Long sessionId; // Can be null if starting a new session
    private String message;
}

package com.learning.englishpro.chatbot.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ChatMessageDto {
    private Long id;
    private Long sessionId;
    private String role;
    private String content;
    private Instant createdAt;
}

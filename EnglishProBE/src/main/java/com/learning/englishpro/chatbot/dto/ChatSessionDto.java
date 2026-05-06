package com.learning.englishpro.chatbot.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class ChatSessionDto {
    private Long id;
    private String title;
    private Instant createdAt;
    private Instant updatedAt;
}

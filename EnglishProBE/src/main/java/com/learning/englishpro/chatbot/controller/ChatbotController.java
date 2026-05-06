package com.learning.englishpro.chatbot.controller;

import com.learning.englishpro.chatbot.dto.ChatMessageDto;
import com.learning.englishpro.chatbot.dto.ChatRequest;
import com.learning.englishpro.chatbot.dto.ChatResponse;
import com.learning.englishpro.chatbot.dto.ChatSessionDto;
import com.learning.englishpro.chatbot.service.ChatbotService;
import com.learning.englishpro.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChatResponse>> sendMessage(
            @RequestBody ChatRequest request,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.learning.englishpro.auth.entity.User user) {
        ChatResponse response = chatbotService.chat(request, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Message sent successfully", response));
    }

    @GetMapping("/sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatSessionDto>>> getUserSessions(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.learning.englishpro.auth.entity.User user) {
        List<ChatSessionDto> sessions = chatbotService.getUserSessions(user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Sessions fetched successfully", sessions));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getSessionMessages(
            @PathVariable Long sessionId,
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.learning.englishpro.auth.entity.User user) {
        List<ChatMessageDto> messages = chatbotService.getSessionMessages(sessionId, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Messages fetched successfully", messages));
    }
}

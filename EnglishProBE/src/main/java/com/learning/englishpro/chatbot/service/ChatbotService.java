package com.learning.englishpro.chatbot.service;

import com.learning.englishpro.ai.dto.GeminiRequest;
import com.learning.englishpro.ai.service.AiService;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.chatbot.dto.ChatMessageDto;
import com.learning.englishpro.chatbot.dto.ChatRequest;
import com.learning.englishpro.chatbot.dto.ChatResponse;
import com.learning.englishpro.chatbot.dto.ChatSessionDto;
import com.learning.englishpro.chatbot.entity.ChatMessage;
import com.learning.englishpro.chatbot.entity.ChatSession;
import com.learning.englishpro.chatbot.repository.ChatMessageRepository;
import com.learning.englishpro.chatbot.repository.ChatSessionRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    private static final String SYSTEM_PROMPT = "You are EnglishPro AI Tutor, a helpful and encouraging educational assistant for students learning English. Your goal is to help them understand grammar, vocabulary, practice conversations, and prepare for exams like IELTS. **IMPORTANT: You MUST ALWAYS reply and explain concepts in Vietnamese (Tiếng Việt)**, because your users do not understand English instructions. Provide translations, explain grammar rules, and give feedback strictly in Vietnamese. Use markdown for better formatting.";

    @Transactional
    public ChatResponse chat(ChatRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        ChatSession session;

        // If no sessionId is provided, create a new session
        if (request.getSessionId() == null) {
            String title = generateTitle(request.getMessage());
            session = ChatSession.builder()
                    .user(user)
                    .title(title)
                    .build();
            session = chatSessionRepository.save(session);
        } else {
            session = chatSessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            
            // Check ownership
            if (!session.getUser().getId().equals(userId)) {
                throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }

        // Save user message
        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .role("user")
                .content(request.getMessage())
                .build();
        chatMessageRepository.save(userMessage);

        // Fetch history (up to last 10 messages for context)
        List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderByIdAsc(session.getId());
        
        // Prepare contents for Gemini API (Keep it under limits by picking last N messages)
        int historyLimit = 10;
        int startIndex = Math.max(0, history.size() - historyLimit);
        
        // Gemini API STRICTLY EXPECTS the first item of chat history to be the "user" role.
        // If startIndex points to a "model" role, we increment it by 1 to skip it.
        if (startIndex < history.size() && "model".equals(history.get(startIndex).getRole())) {
            startIndex++;
        }
        
        List<ChatMessage> recentHistory = history.subList(startIndex, history.size());

        List<GeminiRequest.Content> contents = new ArrayList<>();
        for (ChatMessage msg : recentHistory) {
            contents.add(GeminiRequest.Content.builder()
                    .role(msg.getRole())
                    .parts(Collections.singletonList(new GeminiRequest.Part(msg.getContent())))
                    .build());
        }

        // Call Gemini
        String botReply = aiService.generateChat(SYSTEM_PROMPT, contents, 0.7);

        // Save bot message
        ChatMessage botMessage = ChatMessage.builder()
                .session(session)
                .role("model")
                .content(botReply)
                .build();
        chatMessageRepository.save(botMessage);
        
        // Update session timestamp
        session.setUpdatedAt(Instant.now());
        chatSessionRepository.save(session);

        return ChatResponse.builder()
                .sessionId(session.getId())
                .reply(botReply)
                .build();
    }

    public List<ChatSessionDto> getUserSessions(Long userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(session -> ChatSessionDto.builder()
                        .id(session.getId())
                        .title(session.getTitle())
                        .createdAt(session.getCreatedAt())
                        .updatedAt(session.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ChatMessageDto> getSessionMessages(Long sessionId, Long userId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        if (!session.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return chatMessageRepository.findBySessionIdOrderByIdAsc(sessionId).stream()
                .map(msg -> ChatMessageDto.builder()
                        .id(msg.getId())
                        .sessionId(session.getId())
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private String generateTitle(String firstMessage) {
        if (firstMessage == null || firstMessage.trim().isEmpty()) {
            return "Đoạn chat mới";
        }
        String preview = firstMessage.trim();
        if (preview.length() > 30) {
            return preview.substring(0, 30) + "...";
        }
        return preview;
    }
}

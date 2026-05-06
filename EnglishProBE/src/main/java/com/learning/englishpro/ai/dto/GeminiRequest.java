package com.learning.englishpro.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;
    private SystemInstruction systemInstruction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private String role;
        private List<Part> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemInstruction {
         private List<Part> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationConfig {
        private Double temperature;
        private String responseMimeType;
    }
    
    public static GeminiRequest of(String systemPrompt, String userPrompt, Double temperature) {
        return GeminiRequest.builder()
            .systemInstruction(SystemInstruction.builder()
                .parts(Collections.singletonList(new Part(systemPrompt)))
                .build())
            .contents(Collections.singletonList(Content.builder()
                .role("user")
                .parts(Collections.singletonList(new Part(userPrompt)))
                .build()))
            .generationConfig(GenerationConfig.builder()
                .temperature(temperature)
                .responseMimeType("application/json")
                .build())
            .build();
    }

    public static GeminiRequest ofChat(String systemPrompt, List<Content> contents, Double temperature) {
        return GeminiRequest.builder()
            .systemInstruction(SystemInstruction.builder()
                .parts(Collections.singletonList(new Part(systemPrompt)))
                .build())
            .contents(contents)
            .generationConfig(GenerationConfig.builder()
                .temperature(temperature)
                .responseMimeType("text/plain") // Normal text generation
                .build())
            .build();
    }
}

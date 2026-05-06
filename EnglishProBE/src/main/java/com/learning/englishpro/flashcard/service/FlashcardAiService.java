package com.learning.englishpro.flashcard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.ai.service.AiService;
import com.learning.englishpro.flashcard.dto.FlashcardResponseDto;
import com.learning.englishpro.flashcard.dto.FlashcardsWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashcardAiService {

    private final AiService aiService;
    private final ObjectMapper objectMapper;

    private static final String FLASHCARD_GENERATION_PROMPT = """
        You are an expert English Teacher. Your task is to generate vocabulary flashcards based on a specific topic for Vietnamese learners.
        You MUST output strictly in valid JSON format. Do not include any markdown formatting like ```json or any other text.
        
        Topic: %s
        Number of cards: %d
        
        Rules:
        1. Generate exactly %d words or phrases related to the topic.
        2. For each word, provide:
           - "word": The English word/phrase.
           - "type": The part of speech (noun, verb, adj, adv).
           - "phonetic": The IPA transcription.
           - "definition_en": A simple English explanation.
           - "definition_vi": The Vietnamese translation.
           - "example": A short example sentence.
        3. IMPORTANT — Do NOT generate any word that appears in the following exclusion list (these words already exist in the deck): [%s]
           If a word in the exclusion list is closely related to the topic, choose a synonym or a related word instead.
        
        Output JSON Schema:
        {
          "flashcards": [
            {
              "word": "string",
              "type": "string",
              "phonetic": "string",
              "definition_en": "string",
              "definition_vi": "string",
              "example": "string"
            }
          ]
        }
        """;

    public List<FlashcardResponseDto> generateByTopic(String topic, int count, List<String> existingWords) {
        log.info("Generating {} flashcards for topic: '{}' via AI (excluding {} existing words)...", count, topic, existingWords.size());
        try {
            String exclusionList = existingWords.isEmpty() ? "none" : String.join(", ", existingWords);
            String prompt = String.format(FLASHCARD_GENERATION_PROMPT, topic, count, count, exclusionList);
            String rawJson = aiService.generateJson(prompt, "", 0.7);
            
            // Clean markdown wrappers if any are present
            rawJson = rawJson.replaceAll("```json", "").replaceAll("```", "").trim();
            
            FlashcardsWrapperDto wrapper = objectMapper.readValue(rawJson, FlashcardsWrapperDto.class);
            return wrapper.getFlashcards() != null ? wrapper.getFlashcards() : new ArrayList<>();
        } catch (Exception e) {
            log.error("Failed to generate or parse flashcards from AI response. Exception: ", e);
            throw new RuntimeException("Failed to generate flashcards via AI");
        }
    }
}


package com.learning.englishpro.flashcard.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.flashcard.dto.FlashcardResponseDto;
import com.learning.englishpro.flashcard.dto.GenerateFlashcardRequest;
import com.learning.englishpro.flashcard.service.FlashcardAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/flashcards")
@RequiredArgsConstructor
public class FlashcardAiController {

    private final FlashcardAiService flashcardAiService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FlashcardResponseDto>>> generateFlashcards(@RequestBody GenerateFlashcardRequest request) {
        int count = request.getCount() != null ? request.getCount() : 10;
        List<String> existingWords = request.getExistingWords() != null ? request.getExistingWords() : List.of();
        List<FlashcardResponseDto> flashcards = flashcardAiService.generateByTopic(request.getTopic(), count, existingWords);
        return ResponseEntity.ok(ApiResponse.ok(
                "Generated flashcards successfully",
                flashcards
        ));
    }
}

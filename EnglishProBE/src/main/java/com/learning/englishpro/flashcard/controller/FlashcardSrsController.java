package com.learning.englishpro.flashcard.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.flashcard.dto.request.FlashcardReviewRequest;
import com.learning.englishpro.flashcard.dto.response.DeckProgressResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardReviewTodayResponse;
import com.learning.englishpro.flashcard.service.FlashcardSrsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Flashcard SRS", description = "Spaced Repetition System for Flashcards")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class FlashcardSrsController {

    private final FlashcardSrsService flashcardSrsService;

    @Operation(summary = "[STUDENT] Get flashcards to review today for a deck")
    @GetMapping("/flashcard-decks/{deckId}/review-today")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FlashcardReviewTodayResponse>> getReviewToday(
            @PathVariable Long deckId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardSrsService.getReviewToday(deckId, principal)));
    }

    @Operation(summary = "[STUDENT] Review a flashcard (SM-2 Algorithm)")
    @PostMapping("/flashcards/{flashcardId}/review")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reviewFlashcard(
            @PathVariable Long flashcardId,
            @Valid @RequestBody FlashcardReviewRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        flashcardSrsService.reviewFlashcard(flashcardId, request, principal);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "[STUDENT] Get SRS progress for a deck")
    @GetMapping("/flashcard-decks/{deckId}/progress")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DeckProgressResponse>> getDeckProgress(
            @PathVariable Long deckId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardSrsService.getDeckProgress(deckId, principal)));
    }
}

package com.learning.englishpro.flashcard.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.flashcard.dto.request.AddFlashcardsRequest;
import com.learning.englishpro.flashcard.dto.request.CreateFlashcardDeckRequest;
import com.learning.englishpro.flashcard.dto.request.UpdateFlashcardRequest;
import com.learning.englishpro.flashcard.dto.response.FlashcardDeckDetailResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardDeckSummaryResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardResponse;
import com.learning.englishpro.flashcard.dto.response.FlashcardUploadResponse;
import com.learning.englishpro.flashcard.service.FlashcardService;
import com.learning.englishpro.flashcard.service.FlashcardUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Flashcards", description = "Flashcard Management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;
    private final FlashcardUploadService flashcardUploadService;

    @Operation(summary = "[USER] Get my own flashcard decks")
    @GetMapping("/flashcard-decks/me")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<FlashcardDeckSummaryResponse>>> getMyDecks(
            Pageable pageable,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardService.getMyDecks(principal, pageable)));
    }

    @Operation(summary = "[PUBLIC] Get public flashcard decks")
    @GetMapping("/flashcard-decks")
    public ResponseEntity<ApiResponse<Page<FlashcardDeckSummaryResponse>>> getPublicDecks(
            @RequestParam(required = false) Long courseId,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardService.getPublicDecks(courseId, pageable)));
    }

    @Operation(summary = "[USER] Get flashcard deck detail with cards")
    @GetMapping("/flashcard-decks/{deckId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FlashcardDeckDetailResponse>> getDeckDetail(
            @PathVariable Long deckId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(flashcardService.getDeckDetail(deckId, principal)));
    }

    @Operation(summary = "[USER] Create a new flashcard deck")
    @PostMapping("/flashcard-decks")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FlashcardDeckSummaryResponse>> createDeck(
            @Valid @RequestBody CreateFlashcardDeckRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(flashcardService.createDeck(request, principal)));
    }

    @Operation(summary = "[USER] Add bulk flashcards to a deck")
    @PostMapping("/flashcard-decks/{deckId}/cards")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FlashcardDeckDetailResponse>> addFlashcards(
            @PathVariable Long deckId,
            @Valid @RequestBody AddFlashcardsRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok("Cards added successfully",
                flashcardService.addFlashcards(deckId, request, principal)));
    }

    @Operation(summary = "[USER] Update a single flashcard (partial)")
    @PutMapping("/flashcards/{flashcardId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FlashcardResponse>> updateFlashcard(
            @PathVariable Long flashcardId,
            @Valid @RequestBody UpdateFlashcardRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok("Flashcard updated",
                flashcardService.updateFlashcard(flashcardId, request, principal)));
    }

    @Operation(summary = "[USER] Delete a flashcard")
    @DeleteMapping("/flashcards/{flashcardId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFlashcard(
            @PathVariable Long flashcardId,
            @AuthenticationPrincipal UserDetails principal) {
        flashcardService.deleteFlashcard(flashcardId, principal);
        return ResponseEntity.ok(ApiResponse.noContent("Flashcard deleted"));
    }

    @Operation(summary = "[USER] Delete a flashcard deck")
    @DeleteMapping("/flashcard-decks/{deckId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDeck(
            @PathVariable Long deckId,
            @AuthenticationPrincipal UserDetails principal) {
        flashcardService.deleteDeck(deckId, principal);
        return ResponseEntity.ok(ApiResponse.noContent("Flashcard deck deleted from system"));
    }

    @Operation(summary = "[USER] Upload audio for a flashcard")
    @PostMapping(value = "/flashcards/{flashcardId}/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FlashcardUploadResponse>> uploadFlashcardAudio(
            @PathVariable Long flashcardId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok("Audio uploaded successfully",
                flashcardUploadService.uploadAudio(flashcardId, file, principal)));
    }
}

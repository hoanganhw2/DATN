package com.learning.englishpro.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlashcardReviewTodayResponse(
        int dueCount,
        int newCount,
        int totalToReview,
        List<CardToReview> cards
) {
    public record CardToReview(
            Long flashcardId,
            String frontText,
            String backText,
            String audioUrl,
            Boolean isNew,
            ProgressSummary progress
    ) {}

    public record ProgressSummary(
            Integer intervalDays,
            java.time.LocalDate nextReviewDate,
            String lastGrade
    ) {}
}

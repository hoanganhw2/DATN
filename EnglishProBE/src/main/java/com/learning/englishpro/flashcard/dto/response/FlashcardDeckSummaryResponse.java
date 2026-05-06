package com.learning.englishpro.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.flashcard.entity.FlashcardDeck;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlashcardDeckSummaryResponse(
        Long id,
        Long courseId,
        Long lessonId,
        Long teacherId,
        String title,
        String description,
        Boolean isPublic,
        Integer totalCards,
        Integer learnedCards,
        Integer masteredCards,
        Integer dueToday
) {
    public static FlashcardDeckSummaryResponse from(FlashcardDeck deck) {
        return new FlashcardDeckSummaryResponse(
                deck.getId(),
                deck.getCourse() != null ? deck.getCourse().getId() : null,
                deck.getLesson() != null ? deck.getLesson().getId() : null,
                deck.getTeacher().getId(),
                deck.getTitle(),
                deck.getDescription(),
                deck.getIsPublic(),
                deck.getTotalCards(),
                0,
                0,
                0
        );
    }
}

package com.learning.englishpro.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.flashcard.entity.FlashcardDeck;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlashcardDeckDetailResponse(
        Long id,
        Long courseId,
        Long lessonId,
        Long teacherId,
        String title,
        String description,
        Boolean isPublic,
        Integer totalCards,
        List<FlashcardResponse> flashcards) {
    public static FlashcardDeckDetailResponse from(FlashcardDeck deck) {
        return FlashcardDeckDetailResponse.from(deck, Map.of());
    }

    public static FlashcardDeckDetailResponse from(FlashcardDeck deck, Map<Long, String> statusMap) {
        return new FlashcardDeckDetailResponse(
                deck.getId(),
                deck.getCourse() != null ? deck.getCourse().getId() : null,
                deck.getLesson() != null ? deck.getLesson().getId() : null,
                deck.getTeacher().getId(),
                deck.getTitle(),
                deck.getDescription(),
                deck.getIsPublic(),
                deck.getTotalCards(),
                deck.getFlashcards().stream()
                        .map(card -> FlashcardResponse.from(card, statusMap.getOrDefault(card.getId(), "new")))
                        .collect(Collectors.toList()));
    }
}

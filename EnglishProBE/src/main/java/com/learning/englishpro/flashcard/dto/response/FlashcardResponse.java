package com.learning.englishpro.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.flashcard.entity.Flashcard;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlashcardResponse(
        Long id,
        String frontText,
        String backText,
        String frontImageUrl,
        String backImageUrl,
        String audioUrl,
        String exampleSentence,
        Integer orderIndex,
        String status
) {
    public static FlashcardResponse from(Flashcard card) {
        return from(card, "new");
    }

    public static FlashcardResponse from(Flashcard card, String status) {
        return new FlashcardResponse(
                card.getId(),
                card.getFrontText(),
                card.getBackText(),
                card.getFrontImageUrl(),
                card.getBackImageUrl(),
                card.getAudioUrl(),
                card.getExampleSentence(),
                card.getOrderIndex(),
                status
        );
    }
}

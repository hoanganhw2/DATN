package com.learning.englishpro.flashcard.dto.request;

public record UpdateFlashcardRequest(
        String frontText,
        String backText,
        String exampleSentence,
        Integer orderIndex
) {}

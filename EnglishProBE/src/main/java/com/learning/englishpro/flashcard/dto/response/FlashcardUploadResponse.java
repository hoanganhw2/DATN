package com.learning.englishpro.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FlashcardUploadResponse(
        Long flashcardId,
        String audioUrl
) {}

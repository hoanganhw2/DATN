package com.learning.englishpro.flashcard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeckProgressResponse(
        Integer totalCards,
        Integer newCards,
        Integer learnedCards,
        Integer masteredCards,
        Integer dueToday,
        Instant lastStudiedAt
) {}

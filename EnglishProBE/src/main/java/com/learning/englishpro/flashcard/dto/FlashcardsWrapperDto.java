package com.learning.englishpro.flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardsWrapperDto {
    private List<FlashcardResponseDto> flashcards;
}

package com.learning.englishpro.flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardResponseDto {
    private String word;
    private String type;
    private String phonetic;
    private String definition_en;
    private String definition_vi;
    private String example;
}

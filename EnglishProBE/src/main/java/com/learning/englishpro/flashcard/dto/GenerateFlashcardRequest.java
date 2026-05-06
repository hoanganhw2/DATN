package com.learning.englishpro.flashcard.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerateFlashcardRequest {
    private String topic;
    private Integer count;
    /** Words already existing in the deck — AI will avoid generating duplicates of these. */
    private List<String> existingWords;
}

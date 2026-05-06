package com.learning.englishpro.flashcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FlashcardReviewRequest(
        @NotBlank(message = "Đánh giá không được để trống")
        @Pattern(regexp = "AGAIN|HARD|GOOD|EASY", message = "Đánh giá phải là AGAIN, HARD, GOOD, hoặc EASY")
        String grade
) {}

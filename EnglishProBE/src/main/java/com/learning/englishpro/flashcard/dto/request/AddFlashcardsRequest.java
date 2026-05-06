package com.learning.englishpro.flashcard.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddFlashcardsRequest(
        @NotNull(message = "Danh sách thẻ không được để trống")
        @Size(min = 1, message = "Phải có ít nhất một thẻ")
        @Valid
        List<FlashcardRequest> cards
) {
    public record FlashcardRequest(
            @NotBlank(message = "Mặt trước của thẻ không được để trống")
            String frontText,

            @NotBlank(message = "Mặt sau của thẻ không được để trống")
            String backText,

            /** Câu ví dụ (tùy chọn) */
            String exampleSentence,

            /** URL ảnh mặt trước (tùy chọn) */
            String frontImageUrl,

            /** URL ảnh mặt sau (tùy chọn) */
            String backImageUrl,

            /** URL file âm thanh (tùy chọn) */
            String audioUrl,

            /** Thứ tự thẻ trong bộ (tùy chọn, tự động nếu null) */
            Integer orderIndex
    ) {}
}

package com.learning.englishpro.flashcard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFlashcardDeckRequest(
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
        String title,

        String description,

        /** Nullable: bộ thẻ có thể độc lập, không thuộc khóa học nào */
        Long courseId,

        /** Nullable: bộ thẻ có thể độc lập, không thuộc bài học nào */
        Long lessonId,

        /** Mặc định false (riêng tư) */
        Boolean isPublic
) {}

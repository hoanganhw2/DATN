package com.learning.englishpro.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ChapterRequest(
        @NotBlank(message = "Tiêu đề không được để trống")
        String title,
        
        String description,
        
        @NotNull(message = "Thứ tự không được để trống")
        Integer orderIndex
) {
}

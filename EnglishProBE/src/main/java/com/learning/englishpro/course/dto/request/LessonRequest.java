package com.learning.englishpro.course.dto.request;

import com.learning.englishpro.course.entity.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LessonRequest(
        @NotBlank(message = "Tiêu đề không được để trống")
        String title,
        
        String description,
        
        @NotNull(message = "Loại nội dung không được để trống")
        ContentType contentType,
        
        String contentUrl,
        
        Integer duration,
        
        @NotNull(message = "Thứ tự không được để trống")
        Integer orderIndex,
        
        Boolean isFree
) {
}

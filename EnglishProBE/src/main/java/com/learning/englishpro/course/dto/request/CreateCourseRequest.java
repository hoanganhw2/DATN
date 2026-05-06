package com.learning.englishpro.course.dto.request;

import com.learning.englishpro.course.entity.Level;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCourseRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Slug không được để trống")
    private String slug;

    private String description;

    @NotNull(message = "Cấp độ không được để trống")
    private Level level;

    private String category;

    @Min(value = 0, message = "Giá phải >= 0")
    private BigDecimal price;
    
    private String thumbnailUrl;
}

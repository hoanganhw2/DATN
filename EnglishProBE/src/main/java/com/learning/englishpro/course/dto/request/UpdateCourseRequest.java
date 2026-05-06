package com.learning.englishpro.course.dto.request;

import com.learning.englishpro.course.entity.Level;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateCourseRequest {
    private String title;
    private String slug;
    private String description;
    private Level level;
    private String category;

    @Min(value = 0, message = "Giá phải >= 0")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    private String thumbnailUrl;
    private Boolean isPublished;
}

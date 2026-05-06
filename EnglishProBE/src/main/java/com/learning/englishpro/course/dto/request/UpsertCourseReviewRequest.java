package com.learning.englishpro.course.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertCourseReviewRequest {

    @Min(1)
    @Max(5)
    private int stars;

    @Size(max = 2000)
    private String comment;
}

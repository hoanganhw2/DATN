package com.learning.englishpro.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private int stars;
    private String comment;
    private String createdAt;
    private String updatedAt;
}

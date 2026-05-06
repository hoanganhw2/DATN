package com.learning.englishpro.course.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewCourseApprovalRequest {
    @NotNull(message = "Trạng thái duyệt không được để trống")
    private Boolean approved;
}

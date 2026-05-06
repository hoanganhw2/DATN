package com.learning.englishpro.progress.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollRequest(
        @NotNull(message = "Mã khóa học không được để trống")
        Long courseId
) {}

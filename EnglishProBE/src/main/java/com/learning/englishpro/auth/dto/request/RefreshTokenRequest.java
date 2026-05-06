package com.learning.englishpro.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token không được trống")
        String refreshToken
) {}

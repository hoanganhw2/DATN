package com.learning.englishpro.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Tài khoản (Email/Username) không được trống")
        String email,

        @NotBlank(message = "Mật khẩu không được trống")
        String password
) {}

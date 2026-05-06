package com.learning.englishpro.auth.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateProfileRequest(
        @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
        String fullName,

        @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
        String phone,

        String gender,

        @Size(max = 100, message = "Địa chỉ không được vượt quá 100 ký tự")
        String address,

        LocalDate dateOfBirth,

        @Size(max = 100, message = "Quốc gia không được vượt quá 100 ký tự")
        String country,

        @Size(max = 2000, message = "Mục tiêu học tập không được vượt quá 2000 ký tự")
        String learningGoal
) {}

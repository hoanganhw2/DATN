package com.learning.englishpro.auth.dto.request;

import com.learning.englishpro.auth.entity.Role;
import com.learning.englishpro.auth.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserAdminRequest(
        @NotNull(message = "Vai trò không được để trống")
        Role role,

        @NotNull(message = "Trạng thái không được để trống")
        UserStatus status
) {}

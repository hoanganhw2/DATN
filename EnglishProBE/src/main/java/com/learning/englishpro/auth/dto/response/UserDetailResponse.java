package com.learning.englishpro.auth.dto.response;

import com.learning.englishpro.auth.entity.Role;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.entity.UserStatus;

import java.time.Instant;

public record UserDetailResponse(
        Long id,
        String username,
        String email,
        String fullName,
        Role role,
        UserStatus status,
        Instant createdAt,
        UserProfileDto profile
) {
    public static UserDetailResponse from(User user) {
        UserProfile userProfile = user.getProfile();
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                userProfile != null ? userProfile.getFullName() : null,
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                userProfile != null ? UserProfileDto.from(userProfile) : null
        );
    }
}

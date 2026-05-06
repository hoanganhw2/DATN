package com.learning.englishpro.auth.dto.response;

import com.learning.englishpro.auth.entity.Role;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.entity.UserProfile;
import com.learning.englishpro.auth.entity.UserStatus;

import java.time.Instant;

/**
 * Safe user projection — never leaks password or token data.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String phone,
        String avatarUrl,
        String bio,
        Role role,
        UserStatus status,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        UserProfile profile = user.getProfile();
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                profile != null ? profile.getFullName() : null,
                profile != null ? profile.getPhone() : null,
                profile != null ? profile.getAvatarUrl() : null,
                profile != null ? profile.getBio() : null,
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}

package com.learning.englishpro.auth.dto.response;

import com.learning.englishpro.auth.entity.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,        // seconds
        Long userId,
        String email,
        String username,
        String fullName,
        Role role
) {
    /** Convenience factory — tokenType defaults to "Bearer" */
    public static AuthResponse of(String accessToken, String refreshToken,
                                  long expiresIn,
                                  Long userId, String email,
                                  String username, String fullName, Role role) {
        return new AuthResponse(accessToken, refreshToken, "Bearer",
                expiresIn, userId, email, username, fullName, role);
    }
}

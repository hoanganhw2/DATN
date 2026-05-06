package com.learning.englishpro.auth.dto.response;

import lombok.Builder;

@Builder
public record UserStatsResponse(
    long totalUsers,
    long students,
    long teachers,
    long admins
) {
}

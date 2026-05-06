package com.learning.englishpro.auth.dto.response;

import com.learning.englishpro.auth.entity.UserProfile;
import java.time.LocalDate;

public record UserProfileDto(
        String phone,
        String avatarUrl,
        String gender,
        String address,
        LocalDate dateOfBirth,
        String country,
        String learningGoal
) {
    public static UserProfileDto from(UserProfile profile) {
        return new UserProfileDto(
                profile.getPhone(),
                profile.getAvatarUrl(),
                profile.getGender() != null ? profile.getGender().name() : null,
                profile.getAddress(),
                profile.getDateOfBirth(),
                profile.getCountry(),
                profile.getLearningGoal()
        );
    }
}

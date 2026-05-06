package com.learning.englishpro.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.exercise.entity.Exercise;
import com.learning.englishpro.exercise.entity.ExerciseType;

/**
 * Response DTO for Exercise (without questions).
 * correctAnswer is NEVER exposed here.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExerciseSummaryResponse(
        Long id,
        Long lessonId,
        String title,
        String description,
        ExerciseType type,
        Integer totalPoints,
        Integer timeLimit,
        Integer questionCount
) {
    public static ExerciseSummaryResponse from(Exercise exercise) {
        return new ExerciseSummaryResponse(
                exercise.getId(),
                exercise.getLesson().getId(),
                exercise.getTitle(),
                exercise.getDescription(),
                exercise.getType(),
                exercise.getTotalPoints(),
                exercise.getTimeLimit(),
                exercise.getQuestions().size()
        );
    }
}

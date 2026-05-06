package com.learning.englishpro.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.exercise.entity.Exercise;
import com.learning.englishpro.exercise.entity.ExerciseType;

import java.util.List;

/**
 * Detailed response for Exercise containing questions (WITHOUT correctAnswer).
 * Used for the student "view exercise" endpoint.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExerciseDetailResponse(
        Long id,
        Long lessonId,
        String title,
        String description,
        ExerciseType type,
        Integer totalPoints,
        Integer timeLimit,
        List<QuestionResponse> questions
) {

    public record QuestionResponse(
            Long id,
            String questionText,
            String mediaUrl,
            String questionType,
            List<String> options,   // parsed from JSON
            String explanation,     // available before submit (optional pedagogy)
            Integer points,
            Integer orderIndex
            // correctAnswer intentionally omitted
    ) {}

    public static ExerciseDetailResponse from(Exercise exercise, List<QuestionResponse> questions) {
        return new ExerciseDetailResponse(
                exercise.getId(),
                exercise.getLesson().getId(),
                exercise.getTitle(),
                exercise.getDescription(),
                exercise.getType(),
                exercise.getTotalPoints(),
                exercise.getTimeLimit(),
                questions
        );
    }
}

package com.learning.englishpro.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.exercise.entity.Exercise;
import com.learning.englishpro.exercise.entity.ExerciseType;

import java.util.List;

/**
 * Detailed response for Exercise containing questions WITH correctAnswer.
 * Used for the TEACHER "view exercise" endpoint.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TeacherExerciseDetailResponse(
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
            List<String> options,
            String explanation,
            Integer points,
            Integer orderIndex,
            String correctAnswer 
    ) {}

    public static TeacherExerciseDetailResponse from(Exercise exercise, List<QuestionResponse> questions) {
        return new TeacherExerciseDetailResponse(
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

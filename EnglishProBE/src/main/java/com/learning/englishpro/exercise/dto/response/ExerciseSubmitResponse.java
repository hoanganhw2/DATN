package com.learning.englishpro.exercise.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response returned after a student submits answers.
 * correctAnswer IS included per question so the student can review.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExerciseSubmitResponse(
        Long exerciseId,
        int totalQuestions,
        int correctCount,
        BigDecimal totalScore,
        List<AnswerResult> results
) {

    public record AnswerResult(
            Long questionId,
            String questionText,
            String submittedAnswer,
            String correctAnswer,
            boolean isCorrect,
            BigDecimal score,
            String explanation
    ) {}
}

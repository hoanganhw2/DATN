package com.learning.englishpro.exercise.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for creating/updating an Exercise (TEACHER).
 */
public record CreateExerciseRequest(

        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
        String title,

        String description,

        @NotBlank(message = "Loại bài tập không được để trống")
        String type,

        @Positive(message = "Tổng điểm phải lớn hơn 0")
        Integer totalPoints,

        /** Time limit in seconds */
        Integer timeLimit,

        @NotNull(message = "Danh sách câu hỏi không được để trống")
        @Size(min = 1, message = "Phải có ít nhất một câu hỏi")
        @Valid
        List<QuestionRequest> questions
) {

    public record QuestionRequest(

            @NotBlank(message = "Nội dung câu hỏi không được để trống")
            String questionText,

            String mediaUrl,

            @NotBlank(message = "Loại câu hỏi không được để trống")
            String questionType,

            /** List of answer options; will be serialized to JSON */
            List<String> options,

            @NotBlank(message = "Câu trả lời đúng không được để trống")
            String correctAnswer,

            String explanation,

            @Positive(message = "Điểm số phải lớn hơn 0")
            Integer points,

            Integer orderIndex
    ) {}
}

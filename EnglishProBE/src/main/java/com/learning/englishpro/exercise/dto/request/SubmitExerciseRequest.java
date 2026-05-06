package com.learning.englishpro.exercise.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request body for submitting exercise answers (STUDENT).
 */
public record SubmitExerciseRequest(

        @NotNull(message = "Danh sách câu trả lời không được để trống")
        @Size(min = 1, message = "Phải có ít nhất một câu trả lời")
        @Valid
        List<AnswerItem> answers
) {

    public record AnswerItem(

            @NotNull(message = "Mã câu hỏi không được để trống")
            Long questionId,

            String answerText
    ) {}
}

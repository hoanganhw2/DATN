package com.learning.englishpro.exam.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record UpdateExamRequest(
                @NotBlank(message = "Tiêu đề không được để trống") @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự") String title,

                String description,

                Boolean isPublished,

                @Positive(message = "Thời lượng phải lớn hơn 0") Integer duration,

                @Positive(message = "Tổng điểm phải lớn hơn 0") Integer totalScore,

                BigDecimal passingScore,

                Integer maxAttempts,

                @Valid List<ExamSectionRequest> sections) {
        public record ExamSectionRequest(
                        @NotBlank(message = "Tên phần thi không được để trống") String sectionName,
                        Integer sectionOrder,
                        Integer duration,
                        Integer totalQuestions,
                        @NotNull(message = "Danh sách câu hỏi không được để trống") @Size(min = 1, message = "Phải có ít nhất một câu hỏi") @Valid List<ExamQuestionRequest> questions) {
        }

        public record ExamQuestionRequest(
                        @NotBlank(message = "Nội dung câu hỏi không được để trống") String questionText,
                        String mediaUrl,
                        @NotBlank(message = "Loại câu hỏi không được để trống") String questionType,
                        List<String> options,
                        @NotBlank(message = "Câu trả lời đúng không được để trống") String correctAnswer,
                        String explanation,
                        @Positive(message = "Điểm số phải lớn hơn 0") Integer points,
                        Integer orderIndex) {
        }
}

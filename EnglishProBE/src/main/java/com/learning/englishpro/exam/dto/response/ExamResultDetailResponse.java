package com.learning.englishpro.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.exam.entity.ExamResultStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamResultDetailResponse(
        Long id,
        Long examId,
        String examTitle,
        Integer attemptNumber,
        Instant startTime,
        Instant endTime,
        BigDecimal score,
        Boolean isPassed,
        ExamResultStatus status,
        Instant submittedAt,
        List<SectionDetail> sections
) {
    public record SectionDetail(
            Long sectionId,
            String sectionName,
            List<QuestionDetail> questions
    ) {}

    public record QuestionDetail(
            Long questionId,
            String questionText,
            String mediaUrl,
            String questionType,
            List<String> options,
            String correctAnswer,
            String explanation,
            Integer points,
            String submittedAnswer,
            Boolean isCorrect,
            BigDecimal pointsEarned
    ) {}
}

package com.learning.englishpro.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.exam.entity.ExamResultStatus;
import com.learning.englishpro.exam.entity.UserExamResult;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamResultSummaryResponse(
        Long id,
        Long examId,
        String examTitle,
        Integer attemptNumber,
        Instant startTime,
        Instant endTime,
        BigDecimal score,
        Boolean isPassed,
        ExamResultStatus status,
        Instant submittedAt
) {
    public static ExamResultSummaryResponse from(UserExamResult result) {
        return new ExamResultSummaryResponse(
                result.getId(),
                result.getExam().getId(),
                result.getExam().getTitle(),
                result.getAttemptNumber(),
                result.getStartTime(),
                result.getEndTime(),
                result.getScore(),
                result.getIsPassed(),
                result.getStatus(),
                result.getSubmittedAt()
        );
    }
}

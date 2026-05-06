package com.learning.englishpro.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.exam.entity.Exam;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.exam.entity.ExamType;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamSummaryResponse(
        Long id,
        String title,
        String description,
        ExamType examType,
        Level level,
        Integer duration,
        Integer totalScore,
        BigDecimal passingScore,
        Integer maxAttempts,
        Boolean isPublished
) {
    public static ExamSummaryResponse from(Exam exam) {
        return new ExamSummaryResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getExamType(),
                exam.getLevel(),
                exam.getDuration(),
                exam.getTotalScore(),
                exam.getPassingScore(),
                exam.getMaxAttempts(),
                exam.getIsPublished()
        );
    }
}

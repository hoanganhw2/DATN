package com.learning.englishpro.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubmitExamResponse(
        Long examResultId,
        BigDecimal totalScore,
        Boolean isPassed,
        Instant submittedAt,
        List<SectionResult> sectionResults
) {
    public record SectionResult(
            String sectionName,
            int correct,
            int total,
            BigDecimal score
    ) {}
}

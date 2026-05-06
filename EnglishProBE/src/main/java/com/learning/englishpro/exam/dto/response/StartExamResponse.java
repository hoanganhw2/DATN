package com.learning.englishpro.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StartExamResponse(
        Long examResultId,
        Instant startTime,
        Instant deadline,
        List<SectionResponse> sections
) {
    public record SectionResponse(
            Long sectionId,
            String sectionName,
            Integer sectionOrder,
            Integer duration,
            List<QuestionResponse> questions
    ) {}

    public record QuestionResponse(
            Long questionId,
            String questionText,
            String mediaUrl,
            String questionType,
            List<String> options,
            Integer points,
            Integer orderIndex,
            String submittedAnswer  // Populated when resuming IN_PROGRESS exam
    ) {}
}

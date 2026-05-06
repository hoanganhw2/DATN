package com.learning.englishpro.exam.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.exam.entity.Exam;
import com.learning.englishpro.exam.entity.ExamQuestion;
import com.learning.englishpro.exam.entity.ExamSection;
import com.learning.englishpro.exam.entity.ExamType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamDetailResponse(
        Long id,
        String title,
        String description,
        ExamType examType,
        Level level,
        Integer duration,
        Integer totalScore,
        BigDecimal passingScore,
        Integer maxAttempts,
        Boolean isPublished,
        List<SectionResponse> sections
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SectionResponse(
            Long id,
            String sectionName,
            Integer sectionOrder,
            Integer duration,
            Integer totalQuestions,
            List<QuestionResponse> questions
    ) {
        public static SectionResponse from(ExamSection s, ObjectMapper om) {
            return new SectionResponse(
                    s.getId(),
                    s.getSectionName(),
                    s.getSectionOrder(),
                    s.getDuration(),
                    s.getTotalQuestions(),
                    s.getQuestions().stream()
                            .map(q -> QuestionResponse.from(q, om))
                            .toList()
            );
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record QuestionResponse(
            Long id,
            String questionText,
            String mediaUrl,
            String questionType,
            List<String> options,
            String correctAnswer,
            String explanation,
            Integer points,
            Integer orderIndex
    ) {
        public static QuestionResponse from(ExamQuestion q, ObjectMapper om) {
            List<String> opts = Collections.emptyList();
            if (q.getOptions() != null) {
                try {
                    opts = om.readValue(q.getOptions(), new TypeReference<>() {});
                } catch (Exception ignored) {}
            }
            return new QuestionResponse(
                    q.getId(),
                    q.getQuestionText(),
                    q.getMediaUrl(),
                    q.getQuestionType() != null ? q.getQuestionType().name() : null,
                    opts,
                    q.getCorrectAnswer(),
                    q.getExplanation(),
                    q.getPoints(),
                    q.getOrderIndex()
            );
        }
    }

    public static ExamDetailResponse from(Exam exam, ObjectMapper om) {
        return new ExamDetailResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getDescription(),
                exam.getExamType(),
                exam.getLevel(),
                exam.getDuration(),
                exam.getTotalScore(),
                exam.getPassingScore(),
                exam.getMaxAttempts(),
                exam.getIsPublished(),
                exam.getSections().stream()
                        .map(s -> SectionResponse.from(s, om))
                        .toList()
        );
    }
}

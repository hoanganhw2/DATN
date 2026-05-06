package com.learning.englishpro.progress.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class LessonCompleteResponse {

    private Long    lessonId;
    private Instant completedAt;
    private CourseProgressInfo courseProgress;

    @Data
    @Builder
    public static class CourseProgressInfo {
        private Long       courseId;
        private long       completedLessons;
        private long       totalLessons;
        private BigDecimal progressPercent;
        private boolean    isCompleted;
    }
}

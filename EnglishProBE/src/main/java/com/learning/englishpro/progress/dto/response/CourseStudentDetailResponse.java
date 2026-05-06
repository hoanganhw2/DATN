package com.learning.englishpro.progress.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Full detail of a student's enrollment in a specific course,
 * including lesson-level progress and exercise scores.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStudentDetailResponse {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;

    // ── Enrollment info ───────────────────────────────────────────────────
    private Instant enrolledAt;
    private Instant lastAccessedAt;
    private Instant lastLoginAt;
    private String enrollmentStatus;       // IN_PROGRESS, COMPLETED, EXPIRED
    private BigDecimal progressPercent;     // 0 – 100

    // ── Lesson progress ───────────────────────────────────────────────────
    private int completedLessons;
    private int totalLessons;

    // ── Exercise scores ───────────────────────────────────────────────────
    private List<ExerciseScoreItem> exerciseScores;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseScoreItem {
        private Long exerciseId;
        private String exerciseTitle;
        private String lessonTitle;
        private int totalQuestions;
        private int correctAnswers;
        private BigDecimal scorePercent;    // 0 – 100
        private Instant submittedAt;
    }
}

package com.learning.englishpro.progress.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.course.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_lesson_completion", indexes = {
        @Index(name = "idx_ulc_user_id", columnList = "user_id"),
        @Index(name = "idx_ulc_lesson_id", columnList = "lesson_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserLessonCompletion.UserLessonCompletionId.class)
public class UserLessonCompletion {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    @Builder.Default
    private Instant completedAt = Instant.now();

    /** Thời gian thực tế đã xem (giây) — dùng cho analytics */
    private Integer watchedDurationSecs;

    /** Phần trăm video đã xem — dùng để xác định "hoàn thành" */
    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal watchedPercent = BigDecimal.ZERO;

    // ── Composite PK class ──────────────────────────────────────────────────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLessonCompletionId implements Serializable {
        private Long user;
        private Long lesson;
    }
}

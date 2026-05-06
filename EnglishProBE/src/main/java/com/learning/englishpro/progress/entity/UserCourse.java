package com.learning.englishpro.progress.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "user_courses", indexes = {
        @Index(name = "idx_uc_user_id", columnList = "user_id"),
        @Index(name = "idx_uc_course_id", columnList = "course_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserCourse.UserCourseId.class)
public class UserCourse {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant enrolledAt = Instant.now();

    /** Lần cuối học viên mở khoá học — dùng cho nút "Tiếp tục học" */
    private Instant lastAccessedAt;

    private Instant completedAt;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal progressPercent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.IN_PROGRESS;

    // ── Composite PK class ──────────────────────────────────────────────────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCourseId implements Serializable {
        private Long user;
        private Long course;
    }
}

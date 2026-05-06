package com.learning.englishpro.exam.entity;

import com.learning.englishpro.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_exam_results", indexes = {
        @Index(name = "idx_uer_user_id", columnList = "user_id"),
        @Index(name = "idx_uer_exam_id", columnList = "exam_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    /** Lần thi thứ mấy (bắt đầu từ 1) */
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    private Instant startTime;
    private Instant endTime;

    @Column(precision = 8, scale = 2)
    private BigDecimal score;

    /** Điểm đạt (true/false) — tính dựa theo Exam.passingScore */
    private Boolean isPassed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ExamResultStatus status = ExamResultStatus.IN_PROGRESS;

    private Instant submittedAt;

    @OneToMany(mappedBy = "examResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserExamAnswer> answers = new ArrayList<>();
}

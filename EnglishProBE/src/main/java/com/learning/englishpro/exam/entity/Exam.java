package com.learning.englishpro.exam.entity;

import com.learning.englishpro.common.entity.BaseAuditEntity;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exams", indexes = {
        @Index(name = "idx_exams_course_id", columnList = "course_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Exam extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nullable: exam có thể thuộc một khoá học hoặc độc lập (standalone test).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamType examType;

    @Enumerated(EnumType.STRING)
    private Level level;

    /** Duration in seconds */
    private Integer duration;

    private Integer totalScore;

    /** Ngưỡng điểm đạt (VD: 60.00 tương đương 60%) */
    @Column(precision = 5, scale = 2)
    private BigDecimal passingScore;

    /** Số lần thi tối đa (null = không giới hạn) */
    private Integer maxAttempts;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sectionOrder ASC")
    @Builder.Default
    private List<ExamSection> sections = new ArrayList<>();
}

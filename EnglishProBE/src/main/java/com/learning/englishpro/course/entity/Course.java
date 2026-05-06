package com.learning.englishpro.course.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_courses_teacher_id", columnList = "teacher_id"),
        @Index(name = "idx_courses_slug",       columnList = "slug"),
        @Index(name = "idx_courses_category",   columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Course extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(unique = true, nullable = false, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String thumbnailUrl;

    /** Danh mục: Grammar, Speaking, IELTS, TOEIC... */
    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    /** Giá gốc — để tính và hiển thị % giảm giá */
    @Column(precision = 12, scale = 2)
    private BigDecimal originalPrice;

    /** Giá bán thực tế */
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublished = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CourseApprovalStatus approvalStatus = CourseApprovalStatus.PENDING;

    /** Cached: điểm đánh giá trung bình (cập nhật mỗi khi có review mới) */
    @Column(precision = 3, scale = 1)
    @Builder.Default
    private BigDecimal avgRating = BigDecimal.ZERO;

    /** Cached: tổng số lượt đánh giá */
    @Builder.Default
    private Integer totalReviews = 0;

    /** Cached: tổng số người đã đăng ký */
    @Builder.Default
    private Integer totalEnrollments = 0;

    /** Cached: tổng số bài học trong khoá */
    @Builder.Default
    private Integer totalLessons = 0;

    /** Cached: tổng thời lượng (giây) */
    @Builder.Default
    private Integer totalDurationSecs = 0;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<Chapter> chapters = new ArrayList<>();
}

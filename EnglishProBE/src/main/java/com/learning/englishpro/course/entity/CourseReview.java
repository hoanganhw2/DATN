package com.learning.englishpro.course.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_review_user", columnNames = { "course_id", "user_id" })
}, indexes = {
        @Index(name = "idx_course_reviews_course_id", columnList = "course_id"),
        @Index(name = "idx_course_reviews_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CourseReview extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Số sao từ 1 đến 5 */
    @Column(nullable = false)
    private Integer stars;

    @Column(columnDefinition = "TEXT")
    private String comment;
}

package com.learning.englishpro.progress.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "certificates", indexes = {
        @Index(name = "idx_cert_user_id", columnList = "user_id"),
        @Index(name = "idx_cert_course_id", columnList = "course_id"),
        @Index(name = "idx_cert_code", columnList = "certificate_code", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Mã chứng chỉ duy nhất — dùng để tra cứu & xác minh */
    @Column(name = "certificate_code", nullable = false, unique = true, length = 50)
    private String certificateCode;

    /** Tên đầy đủ của học viên tại thời điểm cấp */
    @Column(nullable = false, length = 200)
    private String studentName;

    /** Tên khóa học tại thời điểm cấp */
    @Column(nullable = false, length = 300)
    private String courseName;

    /** Tên giảng viên tại thời điểm cấp */
    @Column(length = 200)
    private String instructorName;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @PrePersist
    protected void onCreate() {
        if (this.certificateCode == null) {
            this.certificateCode = "EP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.issuedAt == null) {
            this.issuedAt = Instant.now();
        }
    }
}

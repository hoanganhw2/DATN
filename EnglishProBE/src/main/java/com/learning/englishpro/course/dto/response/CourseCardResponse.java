package com.learning.englishpro.course.dto.response;

import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.course.entity.CourseApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCardResponse {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private Level level;
    private BigDecimal price;
    private Boolean isPublished;
    private CourseApprovalStatus approvalStatus;
    private TeacherDto teacher;
    private Integer totalChapters;
    private Integer totalLessons;
    /** Tổng số học viên đã đăng ký khóa học */
    private Integer totalEnrollments;
    /** Điểm trung bình 1–5, làm tròn 1 chữ số thập phân */
    private BigDecimal avgRating;
    /** Số lượt đánh giá */
    private Integer totalReviews;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherDto {
        private Long id;
        private String fullName;
    }
}

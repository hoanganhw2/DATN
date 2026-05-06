package com.learning.englishpro.course.dto.response;

import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.course.entity.CourseApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponse {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private String description;
    private Level level;
    private String category;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Boolean isPublished;
    private CourseApprovalStatus approvalStatus;
    private BigDecimal avgRating;
    private Integer totalReviews;
    private Integer totalEnrollments;
    private Integer totalLessons;
    private Integer totalDurationSecs;
    
    private TeacherDto teacher;
    private List<ChapterResponse> chapters;
    
    private EnrollmentInfo enrollmentInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherDto {
        private Long id;
        private String fullName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrollmentInfo {
        private Boolean isEnrolled;
        private BigDecimal progressPercent;
        private String enrolledAt;
    }
}

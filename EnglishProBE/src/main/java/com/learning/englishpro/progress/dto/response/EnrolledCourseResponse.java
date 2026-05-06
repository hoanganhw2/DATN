package com.learning.englishpro.progress.dto.response;

import com.learning.englishpro.progress.entity.EnrollmentStatus;
import com.learning.englishpro.progress.entity.UserCourse;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class EnrolledCourseResponse {

    private Long       courseId;
    private String     slug;
    private String     title;
    private String     thumbnailUrl;
    private String     teacherName;
    private String     status;          // IN_PROGRESS | COMPLETED | NOT_STARTED
    private BigDecimal progressPercent;
    private long       completedLessons;
    private long       totalLessons;
    private Integer    totalDurationSecs;
    private Instant    enrolledAt;
    private Instant    lastAccessedAt;
    private Instant    completedAt;

    /** Light mapping — completedLessons must be filled separately by the service. */
    public static EnrolledCourseResponse from(UserCourse uc, long completedLessons) {
        var course  = uc.getCourse();
        var teacher = course.getTeacher();
        String teacherName = (teacher != null && teacher.getProfile() != null)
                ? teacher.getProfile().getFullName()
                : (teacher != null ? teacher.getUsername() : null);

        return EnrolledCourseResponse.builder()
                .courseId(course.getId())
                .slug(course.getSlug())
                .title(course.getTitle())
                .thumbnailUrl(course.getThumbnailUrl())
                .teacherName(teacherName)
                .status(uc.getStatus() != null ? uc.getStatus().name() : EnrollmentStatus.IN_PROGRESS.name())
                .progressPercent(uc.getProgressPercent())
                .completedLessons(completedLessons)
                .totalLessons(course.getTotalLessons() != null ? course.getTotalLessons() : 0L)
                .totalDurationSecs(course.getTotalDurationSecs())
                .enrolledAt(uc.getEnrolledAt())
                .lastAccessedAt(uc.getLastAccessedAt())
                .completedAt(uc.getCompletedAt())
                .build();
    }
}

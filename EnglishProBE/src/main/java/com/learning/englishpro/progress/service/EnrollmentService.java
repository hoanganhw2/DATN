package com.learning.englishpro.progress.service;

import com.learning.englishpro.progress.dto.request.EnrollRequest;
import com.learning.englishpro.progress.dto.response.CourseProgressDetailResponse;
import com.learning.englishpro.progress.dto.response.EnrolledCourseResponse;
import com.learning.englishpro.progress.dto.response.LessonCompleteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface EnrollmentService {

    /** POST /api/v1/enrollments */
    EnrolledCourseResponse enroll(EnrollRequest request, UserDetails principal);

    /** GET /api/v1/enrollments */
    Page<EnrolledCourseResponse> getMyEnrollments(UserDetails principal, Pageable pageable);

    /** POST /api/v1/lessons/{lessonId}/complete */
    LessonCompleteResponse completeLesson(Long lessonId, UserDetails principal);

    /** GET /api/v1/enrollments/{courseId}/progress */
    CourseProgressDetailResponse getCourseProgress(Long courseId, UserDetails principal);

    /** GET /api/v1/enrollments/{courseId}/verify-access */
    void verifyLearningAccess(Long courseId, UserDetails principal);
}

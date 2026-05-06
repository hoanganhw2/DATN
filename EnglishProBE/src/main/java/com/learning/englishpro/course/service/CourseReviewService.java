package com.learning.englishpro.course.service;

import com.learning.englishpro.course.dto.request.UpsertCourseReviewRequest;
import com.learning.englishpro.course.dto.response.CourseReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface CourseReviewService {

    Page<CourseReviewResponse> listReviews(Long courseId, Pageable pageable);

    Optional<CourseReviewResponse> getMyReview(Long courseId, UserDetails principal);

    CourseReviewResponse upsertReview(Long courseId, UpsertCourseReviewRequest request, UserDetails principal);

    void deleteMyReview(Long courseId, UserDetails principal);
}

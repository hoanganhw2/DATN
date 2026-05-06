package com.learning.englishpro.course.service.impl;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.dto.request.UpsertCourseReviewRequest;
import com.learning.englishpro.course.dto.response.CourseReviewResponse;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.CourseReview;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.repository.CourseReviewRepository;
import com.learning.englishpro.course.service.CourseReviewService;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseReviewServiceImpl implements CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CourseReviewResponse> listReviews(Long courseId, Pageable pageable) {
        ensurePublishedCourse(courseId);
        return courseReviewRepository.findByCourseIdWithAuthor(courseId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseReviewResponse> getMyReview(Long courseId, UserDetails principal) {
        User user = resolveUser(principal);
        ensurePublishedCourse(courseId);
        return courseReviewRepository.findByCourse_IdAndUser_Id(courseId, user.getId())
                .map(this::toResponse);
    }

    @Override
    public CourseReviewResponse upsertReview(Long courseId, UpsertCourseReviewRequest request, UserDetails principal) {
        User user = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!Boolean.TRUE.equals(course.getIsPublished())) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        if (user.getId().equals(course.getTeacher().getId())) {
            throw new AppException(ErrorCode.TEACHER_CANNOT_REVIEW_OWN_COURSE);
        }

        if (!userCourseRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new AppException(ErrorCode.NOT_ENROLLED);
        }

        CourseReview review = courseReviewRepository.findByCourse_IdAndUser_Id(courseId, user.getId())
                .orElse(CourseReview.builder()
                        .course(course)
                        .user(user)
                        .build());
        review.setStars(request.getStars());
        review.setComment(request.getComment());
        courseReviewRepository.save(review);
        refreshCourseRatingAggregates(courseId);
        return toResponse(review);
    }

    @Override
    public void deleteMyReview(Long courseId, UserDetails principal) {
        User user = resolveUser(principal);
        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
        CourseReview review = courseReviewRepository.findByCourse_IdAndUser_Id(courseId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_REVIEW_NOT_FOUND));
        courseReviewRepository.delete(review);
        refreshCourseRatingAggregates(courseId);
    }

    private void ensurePublishedCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        if (!Boolean.TRUE.equals(course.getIsPublished())) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }
    }

    private User resolveUser(UserDetails principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void refreshCourseRatingAggregates(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        long total = courseReviewRepository.countByCourse_Id(courseId);
        Double avg = courseReviewRepository.averageStarsByCourseId(courseId);
        BigDecimal avgRating = BigDecimal.ZERO;
        if (avg != null && total > 0) {
            avgRating = BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP);
        }
        course.setAvgRating(avgRating);
        course.setTotalReviews((int) total);
        courseRepository.save(course);
    }

    private CourseReviewResponse toResponse(CourseReview r) {
        String fullName = null;
        if (r.getUser() != null && r.getUser().getProfile() != null) {
            fullName = r.getUser().getProfile().getFullName();
        }
        return CourseReviewResponse.builder()
                .id(r.getId())
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .userFullName(fullName)
                .stars(r.getStars())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null)
                .updatedAt(r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : null)
                .build();
    }
}

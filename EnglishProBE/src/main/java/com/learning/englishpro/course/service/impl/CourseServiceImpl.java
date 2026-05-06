package com.learning.englishpro.course.service.impl;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.dto.request.CreateCourseRequest;
import com.learning.englishpro.course.dto.request.ReviewCourseApprovalRequest;
import com.learning.englishpro.course.dto.request.UpdateCourseRequest;
import com.learning.englishpro.course.dto.response.CourseCardResponse;
import com.learning.englishpro.course.dto.response.CourseDetailResponse;
import com.learning.englishpro.course.dto.response.ChapterResponse;
import com.learning.englishpro.course.entity.CourseApprovalStatus;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.payment.repository.OrderRepository;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.service.CourseService;
import com.learning.englishpro.progress.entity.UserCourse;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserCourseRepository userCourseRepository;
    private final OrderRepository orderRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        if (username == null || username.equals("anonymousUser")) {
            return null;
        }
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseCardResponse> getCourses(String keyword, Level level, String category, Pageable pageable) {
        User currentUser = getCurrentUser();
        String role = "USER";
        Long teacherId = null;
        if (currentUser != null) {
            role = currentUser.getRole().name();
            if (role.equals("TEACHER")) {
                teacherId = currentUser.getId();
            }
        }
        Page<Course> courses = courseRepository.findCoursesWithFilter(keyword, level, category, role, teacherId, pageable);
        return courses.map(this::mapToCourseCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseBySlug(String slug) {
        // Thử tìm bằng slug trước
        Optional<Course> courseOpt = courseRepository.findBySlugWithChapters(slug);

        // Nếu không tìm thấy và slug là số → tìm bằng ID (2-step fetch tránh MultipleBagFetchException)
        if (courseOpt.isEmpty()) {
            try {
                Long id = Long.parseLong(slug);
                courseOpt = courseRepository.findByIdWithChapters(id);
                // Bước 2: fetch lessons cho từng chapter (tránh MultipleBagFetch)
                if (courseOpt.isPresent()) {
                    var chaptersWithLessons = courseRepository.findChaptersWithLessonsByCourseId(id);
                    // Hibernate sẽ tự merge vào persistent context
                    courseOpt.get().setChapters(chaptersWithLessons);
                }
            } catch (NumberFormatException ignored) { }
        }

        Course course = courseOpt
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User currentUser = getCurrentUser();

        // PUBLIC course usually must be published, but TEACHER/ADMIN can view
        // unpublished.
        if (!Boolean.TRUE.equals(course.getIsPublished())) {
            if (currentUser == null || (!currentUser.getRole().name().equals("ADMIN")
                    && !currentUser.getId().equals(course.getTeacher().getId()))) {
                throw new AppException(ErrorCode.COURSE_NOT_FOUND);
            }
        }

        CourseDetailResponse response = mapToCourseDetailResponse(course);

        if (currentUser != null) {
            Optional<UserCourse> userCourseOptional = userCourseRepository.findByUserIdAndCourseId(currentUser.getId(),
                    course.getId());
            if (userCourseOptional.isPresent()) {
                UserCourse uc = userCourseOptional.get();
                CourseDetailResponse.EnrollmentInfo info = CourseDetailResponse.EnrollmentInfo.builder()
                        .isEnrolled(true)
                        .progressPercent(uc.getProgressPercent())
                        .enrolledAt(uc.getEnrolledAt().toString())
                        .build();
                response.setEnrollmentInfo(info);
            }
        }

        return response;
    }

    @Override
    public CourseDetailResponse createCourse(CreateCourseRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .description(request.getDescription())
                .level(request.getLevel())
                .category(request.getCategory())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .thumbnailUrl(request.getThumbnailUrl())
                .teacher(currentUser)
                .isPublished(false)
                .approvalStatus(CourseApprovalStatus.PENDING)
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToCourseDetailResponse(savedCourse);
    }

    @Override
    public CourseDetailResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (!currentUser.getRole().name().equals("ADMIN") && !currentUser.getId().equals(course.getTeacher().getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (request.getTitle() != null)
            course.setTitle(request.getTitle());
        if (request.getSlug() != null)
            course.setSlug(request.getSlug());
        if (request.getDescription() != null)
            course.setDescription(request.getDescription());
        if (request.getLevel() != null)
            course.setLevel(request.getLevel());
        if (request.getCategory() != null)
            course.setCategory(request.getCategory());
        if (request.getPrice() != null)
            course.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null)
            course.setOriginalPrice(request.getOriginalPrice());
        if (request.getThumbnailUrl() != null)
            course.setThumbnailUrl(request.getThumbnailUrl());

        // Giảng viên cập nhật nội dung khóa học => quay lại trạng thái chờ duyệt nếu trước đó bị từ chối.
        if (currentUser.getRole().name().equals("TEACHER")) {
            if (CourseApprovalStatus.REJECTED.equals(course.getApprovalStatus())) {
                course.setApprovalStatus(CourseApprovalStatus.PENDING);
            }
            course.setIsPublished(false);
        } else if (request.getIsPublished() != null) {
            course.setIsPublished(request.getIsPublished());
            course.setApprovalStatus(Boolean.TRUE.equals(request.getIsPublished())
                    ? CourseApprovalStatus.APPROVED
                    : CourseApprovalStatus.PENDING);
        }

        Course updatedCourse = courseRepository.save(course);
        return mapToCourseDetailResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (!currentUser.getRole().name().equals("ADMIN") && !currentUser.getId().equals(course.getTeacher().getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (userCourseRepository.countByCourseId(courseId) > 0) {
            throw new AppException(ErrorCode.COURSE_HAS_ENROLLMENTS);
        }

        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseCardResponse> getTeacherCourses(Pageable pageable) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        Page<Course> courses = courseRepository.findByTeacherId(currentUser.getId(), pageable);
        return courses.map(this::mapToCourseCardResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseCardResponse> getPendingCoursesForAdmin(Pageable pageable) {
        User currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.getRole().name().equals("ADMIN")) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        return courseRepository.findByApprovalStatus(CourseApprovalStatus.PENDING, pageable)
                .map(this::mapToCourseCardResponse);
    }

    @Override
    public CourseDetailResponse reviewCourseApproval(Long courseId, ReviewCourseApprovalRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.getRole().name().equals("ADMIN")) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (Boolean.TRUE.equals(request.getApproved())) {
            course.setApprovalStatus(CourseApprovalStatus.APPROVED);
            course.setIsPublished(true);
        } else {
            course.setApprovalStatus(CourseApprovalStatus.REJECTED);
            course.setIsPublished(false);
        }

        return mapToCourseDetailResponse(courseRepository.save(course));
    }

    private CourseCardResponse mapToCourseCardResponse(Course course) {
        long enrolledUsers = userCourseRepository.countByCourseId(course.getId());
        long paidUsers = orderRepository.countDistinctCompletedStudentsByCourseId(course.getId());
        int totalEnrollments = (int) Math.max(enrolledUsers, paidUsers);
        return CourseCardResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .thumbnailUrl(course.getThumbnailUrl())
                .level(course.getLevel())
                .price(course.getPrice())
                .isPublished(course.getIsPublished())
                .approvalStatus(course.getApprovalStatus())
                .teacher(course.getTeacher() != null ? CourseCardResponse.TeacherDto.builder()
                        .id(course.getTeacher().getId())
                        .fullName(course.getTeacher().getProfile() != null
                                ? course.getTeacher().getProfile().getFullName()
                                : null)
                        .build() : null)
                .totalChapters(course.getChapters() != null ? course.getChapters().size() : 0)
                .totalLessons(course.getTotalLessons())
                .totalEnrollments(totalEnrollments)
                .avgRating(course.getAvgRating() != null ? course.getAvgRating() : BigDecimal.ZERO)
                .totalReviews(course.getTotalReviews() != null ? course.getTotalReviews() : 0)
                .build();
    }

    private CourseDetailResponse mapToCourseDetailResponse(Course course) {
        CourseDetailResponse response = CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .thumbnailUrl(course.getThumbnailUrl())
                .description(course.getDescription())
                .level(course.getLevel())
                .category(course.getCategory())
                .price(course.getPrice())
                .originalPrice(course.getOriginalPrice())
                .isPublished(course.getIsPublished())
                .approvalStatus(course.getApprovalStatus())
                .avgRating(course.getAvgRating() != null ? course.getAvgRating() : BigDecimal.ZERO)
                .totalReviews(course.getTotalReviews() != null ? course.getTotalReviews() : 0)
                .totalEnrollments(course.getTotalEnrollments())
                .totalLessons(course.getTotalLessons())
                .totalDurationSecs(course.getTotalDurationSecs())
                .teacher(course.getTeacher() != null ? CourseDetailResponse.TeacherDto.builder()
                        .id(course.getTeacher().getId())
                        .fullName(course.getTeacher().getProfile() != null
                                ? course.getTeacher().getProfile().getFullName()
                                : null)
                        .build() : null)
                .build();

        if (course.getChapters() != null) {
            List<ChapterResponse> chapterResponses = course.getChapters().stream()
                    .map(ChapterResponse::from)
                    .collect(Collectors.toList());
            response.setChapters(chapterResponses);
        }

        return response;
    }
}

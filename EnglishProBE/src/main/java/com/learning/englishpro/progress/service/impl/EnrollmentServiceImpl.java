package com.learning.englishpro.progress.service.impl;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Chapter;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.repository.LessonRepository;
import com.learning.englishpro.payment.entity.OrderStatus;
import com.learning.englishpro.payment.repository.OrderRepository;
import com.learning.englishpro.progress.dto.request.EnrollRequest;
import com.learning.englishpro.progress.dto.response.CourseProgressDetailResponse;
import com.learning.englishpro.progress.dto.response.EnrolledCourseResponse;
import com.learning.englishpro.progress.dto.response.LessonCompleteResponse;
import com.learning.englishpro.progress.entity.EnrollmentStatus;
import com.learning.englishpro.progress.entity.UserCourse;
import com.learning.englishpro.progress.entity.UserLessonCompletion;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import com.learning.englishpro.progress.repository.UserLessonCompletionRepository;
import com.learning.englishpro.progress.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

        private final UserRepository userRepository;
        private final CourseRepository courseRepository;
        private final LessonRepository lessonRepository;
        private final OrderRepository orderRepository;
        private final UserCourseRepository userCourseRepository;
        private final UserLessonCompletionRepository lessonCompletionRepository;

        // ── Helper ───────────────────────────────────────────────────────────────

        private User resolveUser(UserDetails principal) {
                return userRepository.findByUsername(principal.getUsername())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 1. POST /api/v1/enrollments
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional
        public EnrolledCourseResponse enroll(EnrollRequest request, UserDetails principal) {
                User user = resolveUser(principal);
                Course course = courseRepository.findById(request.courseId())
                                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

                // 409 if already enrolled
                if (userCourseRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
                        throw new AppException(ErrorCode.ALREADY_ENROLLED);
                }

                // Paid course → require a COMPLETED order
                if (course.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        boolean paid = orderRepository.existsByUserIdAndCourseIdAndStatus(
                                        user.getId(), course.getId(), OrderStatus.COMPLETED);
                        if (!paid) {
                                throw new AppException(ErrorCode.PAYMENT_REQUIRED);
                        }
                }

                UserCourse uc = UserCourse.builder()
                                .user(user)
                                .course(course)
                                .enrolledAt(Instant.now())
                                .progressPercent(BigDecimal.ZERO)
                                .status(EnrollmentStatus.IN_PROGRESS)
                                .build();

                userCourseRepository.save(uc);

                long enrollments = userCourseRepository.countByCourseId(course.getId());
                course.setTotalEnrollments((int) enrollments);
                courseRepository.save(course);

                log.info("Người dùng {} đã đăng ký khóa học {}", user.getId(), course.getId());
                return EnrolledCourseResponse.from(uc, 0L);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 2. GET /api/v1/enrollments
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional(readOnly = true)
        public Page<EnrolledCourseResponse> getMyEnrollments(UserDetails principal, Pageable pageable) {
                User user = resolveUser(principal);
                return userCourseRepository.findByUserIdWithTeacher(user.getId(), pageable)
                                .map(uc -> {
                                        long completed = lessonCompletionRepository
                                                        .countCompletedLessonsInCourse(user.getId(), uc.getCourse().getId());
                                        return EnrolledCourseResponse.from(uc, completed);
                                });
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 3. POST /api/v1/lessons/{lessonId}/complete
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional
        public LessonCompleteResponse completeLesson(Long lessonId, UserDetails principal) {
                User user = resolveUser(principal);

                var lesson = lessonRepository.findById(lessonId)
                                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

                Course course = lesson.getChapter().getCourse();

                // 403 if not enrolled
                UserCourse userCourse = userCourseRepository
                                .findByUserIdAndCourseId(user.getId(), course.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.NOT_ENROLLED));

                // Idempotent – if already completed, skip insert
                Instant completedAt;
                var existing = lessonCompletionRepository.findByUserIdAndLessonId(user.getId(), lessonId);
                if (existing.isPresent()) {
                        completedAt = existing.get().getCompletedAt();
                } else {
                        UserLessonCompletion completion = UserLessonCompletion.builder()
                                        .user(user)
                                        .lesson(lesson)
                                        .completedAt(Instant.now())
                                        .build();
                        lessonCompletionRepository.save(completion);
                        completedAt = completion.getCompletedAt();
                }

                // Recalculate progress
                long completed = lessonCompletionRepository.countCompletedLessonsInCourse(user.getId(), course.getId());
                long total = userCourseRepository.countTotalLessonsInCourse(course.getId());

                BigDecimal progressPercent = total == 0
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(completed * 100.0 / total).setScale(2, RoundingMode.HALF_UP);

                userCourse.setProgressPercent(progressPercent);

                if (progressPercent.compareTo(BigDecimal.valueOf(100)) >= 0) {
                        userCourse.setCompletedAt(Instant.now());
                        userCourse.setStatus(EnrollmentStatus.COMPLETED);
                }

                userCourseRepository.save(userCourse);

                return LessonCompleteResponse.builder()
                                .lessonId(lessonId)
                                .completedAt(completedAt)
                                .courseProgress(LessonCompleteResponse.CourseProgressInfo.builder()
                                                .courseId(course.getId())
                                                .completedLessons(completed)
                                                .totalLessons(total)
                                                .progressPercent(progressPercent)
                                                .isCompleted(userCourse.getCompletedAt() != null)
                                                .build())
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 4. GET /api/v1/enrollments/{courseId}/progress
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional(readOnly = true)
        public CourseProgressDetailResponse getCourseProgress(Long courseId, UserDetails principal) {
                User user = resolveUser(principal);
                // 2-step fetch tránh MultipleBagFetchException
                Course course = courseRepository.findByIdWithChapters(courseId)
                                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
                course.setChapters(courseRepository.findChaptersWithLessonsByCourseId(courseId));

                // 403 if not enrolled
                if (!userCourseRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
                        throw new AppException(ErrorCode.NOT_ENROLLED);
                }

                List<CourseProgressDetailResponse.ChapterProgressInfo> chapterInfos = course.getChapters()
                                .stream()
                                .map(chapter -> buildChapterInfo(chapter, user.getId()))
                                .toList();

                return CourseProgressDetailResponse.builder()
                                .courseId(course.getId())
                                .courseTitle(course.getTitle())
                                .chapters(chapterInfos)
                                .build();
        }

        private CourseProgressDetailResponse.ChapterProgressInfo buildChapterInfo(Chapter chapter, Long userId) {
                long total = chapter.getLessons().size();
                long completed = lessonCompletionRepository.countCompletedLessonsInChapter(userId, chapter.getId());
                List<Long> completedIds = lessonCompletionRepository.findCompletedLessonIdsByChapter(userId, chapter.getId());

                return CourseProgressDetailResponse.ChapterProgressInfo.builder()
                                .chapterId(chapter.getId())
                                .title(chapter.getTitle())
                                .completedLessons(completed)
                                .totalLessons(total)
                                .isCompleted(total > 0 && completed >= total)
                                .completedLessonIds(completedIds)
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 5. GET /api/v1/enrollments/{courseId}/verify-access
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional(readOnly = true)
        public void verifyLearningAccess(Long courseId, UserDetails principal) {
                User user = resolveUser(principal);
                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

                // Khóa học trả phí → kiểm tra đã thanh toán chưa
                if (course.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
                        boolean paid = orderRepository.existsByUserIdAndCourseIdAndStatus(
                                        user.getId(), course.getId(), OrderStatus.COMPLETED);
                        if (!paid) {
                                throw new AppException(ErrorCode.PAYMENT_REQUIRED);
                        }
                }

                // Kiểm tra đã enroll chưa
                if (!userCourseRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
                        throw new AppException(ErrorCode.NOT_ENROLLED);
                }
        }
}

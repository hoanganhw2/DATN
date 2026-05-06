package com.learning.englishpro.course.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.dto.request.LessonRequest;
import com.learning.englishpro.course.dto.response.LessonResponse;
import com.learning.englishpro.course.entity.Chapter;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.repository.ChapterRepository;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.repository.LessonRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void checkCourseOwnership(Course course, User currentUser) {
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    /** Recount all lessons for a course and persist the value to Course.totalLessons */
    private void syncTotalLessons(Course course) {
        // Flush pending inserts/deletes so the count query sees the latest state
        entityManager.flush();
        int count = lessonRepository.countByChapter_Course_Id(course.getId());
        course.setTotalLessons(count);
        courseRepository.save(course);
    }

    public LessonResponse createLesson(UserDetails principal, Long courseId, Long chapterId, LessonRequest request) {
        User currentUser = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        
        checkCourseOwnership(course, currentUser);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));

        if (!chapter.getCourse().getId().equals(courseId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        Lesson lesson = Lesson.builder()
                .chapter(chapter)
                .title(request.title())
                .description(request.description())
                .contentType(request.contentType())
                .contentUrl(request.contentUrl())
                .duration(request.duration())
                .orderIndex(request.orderIndex())
                .isFree(request.isFree() != null ? request.isFree() : false)
                .build();

        LessonResponse response = LessonResponse.from(lessonRepository.save(lesson));
        // Keep totalLessons in sync after adding a new lesson
        syncTotalLessons(course);
        return response;
    }

    public LessonResponse updateLesson(UserDetails principal, Long lessonId, LessonRequest request) {
        User currentUser = resolveUser(principal);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        checkCourseOwnership(lesson.getChapter().getCourse(), currentUser);

        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setContentType(request.contentType());
        // Only overwrite contentUrl if provided; prevents accidentally clearing a previously uploaded file
        if (request.contentUrl() != null && !request.contentUrl().isBlank()) {
            lesson.setContentUrl(request.contentUrl());
        }
        lesson.setDuration(request.duration());
        lesson.setOrderIndex(request.orderIndex());
        if (request.isFree() != null) {
            lesson.setIsFree(request.isFree());
        }

        return LessonResponse.from(lessonRepository.save(lesson));
    }

    public void deleteLesson(UserDetails principal, Long lessonId) {
        User currentUser = resolveUser(principal);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        Course course = lesson.getChapter().getCourse();
        checkCourseOwnership(course, currentUser);

        lessonRepository.delete(lesson);
        // Keep totalLessons in sync after removing a lesson
        syncTotalLessons(course);
    }
}

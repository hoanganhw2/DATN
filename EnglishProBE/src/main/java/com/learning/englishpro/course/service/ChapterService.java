package com.learning.englishpro.course.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.dto.request.ChapterRequest;
import com.learning.englishpro.course.dto.request.ReorderRequest;
import com.learning.englishpro.course.dto.response.ChapterResponse;
import com.learning.englishpro.course.entity.Chapter;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.Lesson;
import com.learning.englishpro.course.repository.ChapterRepository;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.course.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private void checkCourseOwnership(Course course, User currentUser) {
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
    }

    public ChapterResponse createChapter(UserDetails principal, Long courseId, ChapterRequest request) {
        User currentUser = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        
        checkCourseOwnership(course, currentUser);

        Chapter chapter = Chapter.builder()
                .course(course)
                .title(request.title())
                .description(request.description())
                .orderIndex(request.orderIndex())
                .build();
        
        return ChapterResponse.from(chapterRepository.save(chapter));
    }

    public ChapterResponse updateChapter(UserDetails principal, Long courseId, Long chapterId, ChapterRequest request) {
        User currentUser = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        
        checkCourseOwnership(course, currentUser);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
                
        if (!chapter.getCourse().getId().equals(courseId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        chapter.setTitle(request.title());
        chapter.setDescription(request.description());
        chapter.setOrderIndex(request.orderIndex());

        return ChapterResponse.from(chapterRepository.save(chapter));
    }

    public void deleteChapter(UserDetails principal, Long courseId, Long chapterId) {
        User currentUser = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        
        checkCourseOwnership(course, currentUser);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAPTER_NOT_FOUND));
                
        if (!chapter.getCourse().getId().equals(courseId)) {
            throw new AppException(ErrorCode.VALIDATION_FAILED);
        }

        chapterRepository.delete(chapter);
    }

    /**
     * Batch-update orderIndex for chapters and lessons in a single transaction.
     * Validates that all affected entities belong to the given course.
     */
    public void reorderCurriculum(UserDetails principal, Long courseId, ReorderRequest request) {
        User currentUser = resolveUser(principal);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        checkCourseOwnership(course, currentUser);

        // Reorder chapters
        if (request.chapters() != null && !request.chapters().isEmpty()) {
            Map<Long, Integer> chapterOrderMap = request.chapters().stream()
                    .collect(Collectors.toMap(ReorderRequest.ReorderItem::id, ReorderRequest.ReorderItem::orderIndex));

            var chaptersToUpdate = chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
            for (Chapter ch : chaptersToUpdate) {
                Integer newOrder = chapterOrderMap.get(ch.getId());
                if (newOrder != null) {
                    ch.setOrderIndex(newOrder);
                }
            }
            chapterRepository.saveAll(chaptersToUpdate);
        }

        // Reorder lessons
        if (request.lessons() != null && !request.lessons().isEmpty()) {
            var lessonIds = request.lessons().stream()
                    .map(ReorderRequest.ReorderItem::id)
                    .collect(Collectors.toList());

            var lessonsToUpdate = lessonRepository.findAllById(lessonIds);

            Map<Long, Integer> lessonOrderMap = request.lessons().stream()
                    .collect(Collectors.toMap(ReorderRequest.ReorderItem::id, ReorderRequest.ReorderItem::orderIndex));

            for (Lesson lesson : lessonsToUpdate) {
                // Verify lesson belongs to this course
                if (!lesson.getChapter().getCourse().getId().equals(courseId)) {
                    throw new AppException(ErrorCode.ACCESS_DENIED);
                }
                Integer newOrder = lessonOrderMap.get(lesson.getId());
                if (newOrder != null) {
                    lesson.setOrderIndex(newOrder);
                }
            }
            lessonRepository.saveAll(lessonsToUpdate);
        }
    }
}


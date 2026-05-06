package com.learning.englishpro.progress.repository;

import com.learning.englishpro.progress.entity.UserLessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonCompletionRepository
        extends JpaRepository<UserLessonCompletion, UserLessonCompletion.UserLessonCompletionId> {

    Optional<UserLessonCompletion> findByUserIdAndLessonId(Long userId, Long lessonId);

    /** Count how many lessons in a given course the user has completed. */
    @Query("""
            SELECT COUNT(ulc)
            FROM UserLessonCompletion ulc
            WHERE ulc.user.id   = :userId
              AND ulc.lesson.chapter.course.id = :courseId
            """)
    long countCompletedLessonsInCourse(@Param("userId") Long userId,
                                       @Param("courseId") Long courseId);

    /** Count how many lessons in a given chapter the user has completed. */
    @Query("""
            SELECT COUNT(ulc)
            FROM UserLessonCompletion ulc
            WHERE ulc.user.id          = :userId
              AND ulc.lesson.chapter.id = :chapterId
            """)
    long countCompletedLessonsInChapter(@Param("userId") Long userId,
                                        @Param("chapterId") Long chapterId);

    /** Return the actual lesson IDs completed by a user in a given chapter. */
    @Query("""
            SELECT ulc.lesson.id
            FROM UserLessonCompletion ulc
            WHERE ulc.user.id          = :userId
              AND ulc.lesson.chapter.id = :chapterId
            """)
    List<Long> findCompletedLessonIdsByChapter(@Param("userId") Long userId,
                                               @Param("chapterId") Long chapterId);
}


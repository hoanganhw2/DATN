package com.learning.englishpro.progress.repository;

import com.learning.englishpro.progress.entity.UserCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, UserCourse.UserCourseId> {

    @Query("SELECT uc FROM UserCourse uc WHERE uc.user.id = :userId AND uc.course.id = :courseId")
    Optional<UserCourse> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT COUNT(uc) > 0 FROM UserCourse uc WHERE uc.user.id = :userId AND uc.course.id = :courseId")
    boolean existsByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /** Fetch all enrollments of a user with the associated course eagerly loaded. */
    @Query("""
            SELECT uc FROM UserCourse uc
            JOIN FETCH uc.course c
            WHERE uc.user.id = :userId
            ORDER BY uc.enrolledAt DESC
            """)
    Page<UserCourse> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /** Same as findByUserId but also eagerly loads teacher + profile for display. */
    @Query(
        value = """
            SELECT uc FROM UserCourse uc
            JOIN FETCH uc.course c
            JOIN FETCH c.teacher t
            LEFT JOIN FETCH t.profile
            WHERE uc.user.id = :userId
            ORDER BY uc.enrolledAt DESC
            """,
        countQuery = """
            SELECT COUNT(uc)
            FROM UserCourse uc
            WHERE uc.user.id = :userId
            """
    )
    Page<UserCourse> findByUserIdWithTeacher(@Param("userId") Long userId, Pageable pageable);

    /** Count total lessons in a course (for progress percent calculation). */
    @Query("""
            SELECT COUNT(l)
            FROM Lesson l
            WHERE l.chapter.course.id = :courseId
            """)
    long countTotalLessonsInCourse(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(uc) FROM UserCourse uc WHERE uc.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);

    /** Fetch all enrollments for a specific course with user & profile eagerly loaded. */
    @Query("""
            SELECT uc FROM UserCourse uc
            JOIN FETCH uc.user u
            LEFT JOIN FETCH u.profile
            WHERE uc.course.id = :courseId
            ORDER BY uc.enrolledAt DESC
            """)
    List<UserCourse> findByCourseIdWithUser(@Param("courseId") Long courseId);

    @Query("""
            SELECT uc FROM UserCourse uc
            JOIN FETCH uc.user u
            JOIN FETCH uc.course c
            WHERE uc.status = 'IN_PROGRESS'
              AND (
                  (uc.lastAccessedAt IS NOT NULL AND uc.lastAccessedAt BETWEEN :startTime AND :endTime)
                  OR 
                  (uc.lastAccessedAt IS NULL AND uc.enrolledAt BETWEEN :startTime AND :endTime)
              )
            """)
    List<UserCourse> findIdleEnrollments(@Param("startTime") java.time.Instant startTime, @Param("endTime") java.time.Instant endTime);
}


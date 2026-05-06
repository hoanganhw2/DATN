package com.learning.englishpro.course.repository;

import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.entity.CourseApprovalStatus;
import com.learning.englishpro.course.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

     @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:level IS NULL OR c.level = :level) AND " +
            "(:category IS NULL OR c.category = :category) AND " +
            "(c.isPublished = true OR :role = 'ADMIN' OR c.teacher.id = :teacherId)")
     Page<Course> findCoursesWithFilter(@Param("keyword") String keyword,
                                        @Param("level") Level level,
                                        @Param("category") String category,
                                        @Param("role") String role,
                                        @Param("teacherId") Long teacherId,
                                        Pageable pageable);

    // Query 1: fetch Course + chapters only
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.chapters " +
           "WHERE c.slug = :slug")
    Optional<Course> findBySlugWithChapters(@Param("slug") String slug);

    // Query 2: fetch Course + chapters + lessons (separate query to avoid MultipleBagFetchException)
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.chapters ch " +
           "LEFT JOIN FETCH ch.lessons " +
           "WHERE c.slug = :slug")
    Optional<Course> findBySlugWithChaptersAndLessons(@Param("slug") String slug);

    // Tìm Course + chapters by ID (bước 1 — chỉ fetch chapters)
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.chapters " +
           "WHERE c.id = :id")
    Optional<Course> findByIdWithChapters(@Param("id") Long id);

    // Tìm Course + chapters + lessons by ID (bước 2 — fetch lessons sau khi chapters đã load)
    @Query("SELECT DISTINCT ch FROM Chapter ch " +
           "LEFT JOIN FETCH ch.lessons " +
           "WHERE ch.course.id = :courseId " +
           "ORDER BY ch.orderIndex ASC")
    java.util.List<com.learning.englishpro.course.entity.Chapter> findChaptersWithLessonsByCourseId(@Param("courseId") Long courseId);

    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);

    Page<Course> findByApprovalStatus(CourseApprovalStatus approvalStatus, Pageable pageable);
}

package com.learning.englishpro.course.repository;

import com.learning.englishpro.course.entity.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    Optional<CourseReview> findByCourse_IdAndUser_Id(Long courseId, Long userId);

    @Query(value = """
            SELECT r FROM CourseReview r
            JOIN FETCH r.user u
            LEFT JOIN FETCH u.profile
            WHERE r.course.id = :courseId
            """,
            countQuery = "SELECT COUNT(r) FROM CourseReview r WHERE r.course.id = :courseId")
    Page<CourseReview> findByCourseIdWithAuthor(@Param("courseId") Long courseId, Pageable pageable);

    long countByCourse_Id(Long courseId);

    @Query("SELECT AVG(r.stars) FROM CourseReview r WHERE r.course.id = :courseId")
    Double averageStarsByCourseId(@Param("courseId") Long courseId);
}

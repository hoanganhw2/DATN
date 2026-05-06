package com.learning.englishpro.exercise.repository;

import com.learning.englishpro.exercise.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("SELECT e FROM Exercise e WHERE e.lesson.id = :lessonId ORDER BY e.id ASC")
    List<Exercise> findByLessonId(@Param("lessonId") Long lessonId);

    /** Find all exercises for a given course, fetching lesson + questions for score calculation. */
    @Query("""
            SELECT DISTINCT e FROM Exercise e
            JOIN FETCH e.lesson l
            JOIN FETCH e.questions
            WHERE l.chapter.course.id = :courseId
            ORDER BY l.orderIndex ASC, e.id ASC
            """)
    List<Exercise> findByCourseIdWithQuestions(@Param("courseId") Long courseId);
}


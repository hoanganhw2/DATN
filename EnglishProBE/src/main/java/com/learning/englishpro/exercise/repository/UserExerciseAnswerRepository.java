package com.learning.englishpro.exercise.repository;

import com.learning.englishpro.exercise.entity.UserExerciseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserExerciseAnswerRepository extends JpaRepository<UserExerciseAnswer, Long> {

    Optional<UserExerciseAnswer> findByUserIdAndExerciseIdAndQuestionId(
            Long userId, Long exerciseId, Long questionId);

    List<UserExerciseAnswer> findByUserIdAndExerciseId(Long userId, Long exerciseId);

    void deleteByExerciseId(Long exerciseId);

    /** Find all answers of a user for exercises within a specific course. */
    @Query("""
            SELECT uea FROM UserExerciseAnswer uea
            JOIN FETCH uea.exercise e
            WHERE uea.user.id = :userId
              AND e.lesson.chapter.course.id = :courseId
            """)
    List<UserExerciseAnswer> findByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);
}


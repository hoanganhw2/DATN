package com.learning.englishpro.exercise.repository;

import com.learning.englishpro.exercise.entity.ExerciseQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseQuestionRepository extends JpaRepository<ExerciseQuestion, Long> {
}

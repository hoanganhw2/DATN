package com.learning.englishpro.exam.repository;

import com.learning.englishpro.exam.entity.UserExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserExamAnswerRepository extends JpaRepository<UserExamAnswer, Long> {
    Optional<UserExamAnswer> findByExamResultIdAndQuestionId(Long examResultId, Long questionId);
}

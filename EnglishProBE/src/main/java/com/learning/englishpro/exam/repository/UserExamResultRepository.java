package com.learning.englishpro.exam.repository;

import com.learning.englishpro.exam.entity.ExamResultStatus;
import com.learning.englishpro.exam.entity.UserExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserExamResultRepository extends JpaRepository<UserExamResult, Long> {

    Optional<UserExamResult> findFirstByUserIdAndExamIdAndStatus(Long userId, Long examId, ExamResultStatus status);

    int countByUserIdAndExamId(Long userId, Long examId);

    Page<UserExamResult> findByUserIdAndExamIdOrderByStartTimeDesc(Long userId, Long examId, Pageable pageable);
}

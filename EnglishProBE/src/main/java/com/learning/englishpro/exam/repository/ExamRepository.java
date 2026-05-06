package com.learning.englishpro.exam.repository;

import com.learning.englishpro.exam.entity.Exam;
import com.learning.englishpro.exam.entity.ExamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Page<Exam> findByIsPublishedTrue(Pageable pageable);
    
    Page<Exam> findByExamTypeAndIsPublishedTrue(ExamType examType, Pageable pageable);

    Page<Exam> findByIsPublishedTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Exam> findByExamTypeAndIsPublishedTrueAndTitleContainingIgnoreCase(ExamType examType, String title, Pageable pageable);

    Page<Exam> findByCreatorId(Long creatorId, Pageable pageable);
}

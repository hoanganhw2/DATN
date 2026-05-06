package com.learning.englishpro.exam.repository;

import com.learning.englishpro.exam.entity.EssaySubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EssaySubmissionRepository extends JpaRepository<EssaySubmission, Long> {
}

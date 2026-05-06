package com.learning.englishpro.exam.repository;

import com.learning.englishpro.exam.entity.ExamSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamSectionRepository extends JpaRepository<ExamSection, Long> {
}

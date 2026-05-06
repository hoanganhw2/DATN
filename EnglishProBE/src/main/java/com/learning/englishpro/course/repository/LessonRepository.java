package com.learning.englishpro.course.repository;

import com.learning.englishpro.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    int countByChapter_Course_Id(Long courseId);

}

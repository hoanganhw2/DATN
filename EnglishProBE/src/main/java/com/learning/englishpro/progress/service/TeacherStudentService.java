package com.learning.englishpro.progress.service;

import com.learning.englishpro.progress.dto.response.CourseStudentDetailResponse;

import java.util.List;

public interface TeacherStudentService {

    /**
     * Get all students enrolled in a course, including progress and exercise scores.
     * Only the course owner (teacher) may call this.
     */
    List<CourseStudentDetailResponse> getStudentsByCourse(Long courseId, Long teacherId);
}

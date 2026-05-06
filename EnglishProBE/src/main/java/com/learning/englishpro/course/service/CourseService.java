package com.learning.englishpro.course.service;

import com.learning.englishpro.course.dto.request.CreateCourseRequest;
import com.learning.englishpro.course.dto.request.ReviewCourseApprovalRequest;
import com.learning.englishpro.course.dto.request.UpdateCourseRequest;
import com.learning.englishpro.course.dto.response.CourseCardResponse;
import com.learning.englishpro.course.dto.response.CourseDetailResponse;
import com.learning.englishpro.course.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    Page<CourseCardResponse> getCourses(String keyword, Level level, String category, Pageable pageable);
    
    CourseDetailResponse getCourseBySlug(String slug);
    
    CourseDetailResponse createCourse(CreateCourseRequest request);
    
    CourseDetailResponse updateCourse(Long courseId, UpdateCourseRequest request);
    
    void deleteCourse(Long courseId);
    
    Page<CourseCardResponse> getTeacherCourses(Pageable pageable);

    Page<CourseCardResponse> getPendingCoursesForAdmin(Pageable pageable);

    CourseDetailResponse reviewCourseApproval(Long courseId, ReviewCourseApprovalRequest request);
}

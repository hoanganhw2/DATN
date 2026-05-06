package com.learning.englishpro.course.controller;



import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.course.dto.request.CreateCourseRequest;
import com.learning.englishpro.course.dto.request.ReviewCourseApprovalRequest;
import com.learning.englishpro.course.dto.request.UpdateCourseRequest;
import com.learning.englishpro.course.dto.response.CourseCardResponse;
import com.learning.englishpro.course.dto.response.CourseDetailResponse;
import com.learning.englishpro.course.dto.response.ThumbnailUploadResponse;
import com.learning.englishpro.course.entity.Level;
import com.learning.englishpro.course.service.CourseService;
import com.learning.englishpro.course.service.CourseUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Courses", description = "Course Management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseUploadService courseUploadService;

    // 1. GET /api/v1/courses?page&size&level&keyword [PUBLIC]
    @GetMapping()
    public ResponseEntity<ApiResponse<Page<CourseCardResponse>>> getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Level level,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseCardResponse> courses = courseService.getCourses(keyword, level, category, pageable);
        return ResponseEntity.ok(ApiResponse.ok( "Courses retrieved successfully",courses));
    }

    // 2. GET /api/v1/courses/{slug} [PUBLIC]
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseDetail(@PathVariable String slug) {
        CourseDetailResponse course = courseService.getCourseBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok( course));
    }

    // 3. POST /api/v1/courses [TEACHER]
    @PostMapping("/")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request) {
        CourseDetailResponse createdCourse = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(createdCourse));
    }

    // 4. PUT /api/v1/courses/{id} [TEACHER/ADMIN]
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        CourseDetailResponse updatedCourse = courseService.updateCourse(id, request);
        return ResponseEntity.ok(ApiResponse.ok( "Course updated successfully",updatedCourse));
    }

    // 5. DELETE /api/v1/courses/{id} [TEACHER/ADMIN]
    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.ok( "Course deleted successfully",null));
    }

    // 6. GET /api/v1/teacher/courses [TEACHER]
    @GetMapping("/teacher/courses")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Page<CourseCardResponse>>> getTeacherCourses(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseCardResponse> courses = courseService.getTeacherCourses(pageable);
        return ResponseEntity.ok(ApiResponse.ok( "Teacher courses retrieved successfully",courses));
    }

    // 6.1 GET /api/v1/courses/admin/pending [ADMIN]
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<CourseCardResponse>>> getPendingCoursesForAdmin(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseCardResponse> courses = courseService.getPendingCoursesForAdmin(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Pending courses retrieved successfully", courses));
    }

    // 6.2 PUT /api/v1/courses/{id}/approval [ADMIN]
    @PutMapping("/{id}/approval")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> reviewCourseApproval(
            @PathVariable Long id,
            @Valid @RequestBody ReviewCourseApprovalRequest request) {
        CourseDetailResponse reviewed = courseService.reviewCourseApproval(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Course approval reviewed successfully", reviewed));
    }

    // 7. POST /api/v1/courses/{id}/thumbnail [TEACHER]
    @Operation(summary = "[TEACHER] Upload course thumbnail image")
    @PostMapping(value = "/{id}/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<ThumbnailUploadResponse>> uploadThumbnail(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        ThumbnailUploadResponse response = courseUploadService.uploadThumbnail(id, file, principal);
        return ResponseEntity.ok(ApiResponse.ok("Thumbnail uploaded successfully", response));
    }
}

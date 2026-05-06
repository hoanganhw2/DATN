package com.learning.englishpro.progress.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.progress.dto.request.EnrollRequest;
import com.learning.englishpro.progress.dto.response.CourseProgressDetailResponse;
import com.learning.englishpro.progress.dto.response.EnrolledCourseResponse;
import com.learning.englishpro.progress.dto.response.LessonCompleteResponse;
import com.learning.englishpro.progress.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Enrollments", description = "Learning progress & enrollment management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // ── 1. POST /api/v1/enrollments ──────────────────────────────────────────

    @Operation(summary = "[STUDENT] Enroll in a course")
    @PostMapping("/enrollments")
    @PreAuthorize("hasAnyRole('STUDENT', 'USER')")
    public ResponseEntity<ApiResponse<EnrolledCourseResponse>> enroll(
            @Valid @RequestBody EnrollRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        EnrolledCourseResponse response = enrollmentService.enroll(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    // ── 2. GET /api/v1/enrollments ───────────────────────────────────────────

    @Operation(summary = "[STUDENT] List my enrolled courses")
    @GetMapping("/enrollments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<EnrolledCourseResponse>>> getMyEnrollments(
            @AuthenticationPrincipal UserDetails principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<EnrolledCourseResponse> data = enrollmentService.getMyEnrollments(principal, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Enrollments retrieved successfully", data));
    }

    // ── 3. POST /api/v1/lessons/{lessonId}/complete ──────────────────────────

    @Operation(summary = "[STUDENT] Mark a lesson as complete")
    @PostMapping("/lessons/{lessonId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonCompleteResponse>> completeLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails principal) {
        LessonCompleteResponse response = enrollmentService.completeLesson(lessonId, principal);
        return ResponseEntity.ok(ApiResponse.ok("Lesson marked as complete", response));
    }

    // ── 4. GET /api/v1/enrollments/{courseId}/progress ───────────────────────

    @Operation(summary = "[STUDENT] Get detailed chapter-level progress for a course")
    @GetMapping("/enrollments/{courseId}/progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseProgressDetailResponse>> getCourseProgress(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        CourseProgressDetailResponse response = enrollmentService.getCourseProgress(courseId, principal);
        return ResponseEntity.ok(ApiResponse.ok("Course progress retrieved successfully", response));
    }

    // ── 5. GET /api/v1/enrollments/{courseId}/verify-access ──────────────────

    @Operation(summary = "[STUDENT] Verify access to a course learning page (must have purchased)")
    @GetMapping("/enrollments/{courseId}/verify-access")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> verifyLearningAccess(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        enrollmentService.verifyLearningAccess(courseId, principal);
        return ResponseEntity.ok(ApiResponse.ok(java.util.Map.of("hasAccess", true)));
    }
}

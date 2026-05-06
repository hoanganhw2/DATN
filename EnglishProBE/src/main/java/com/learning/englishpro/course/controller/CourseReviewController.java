package com.learning.englishpro.course.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.course.dto.request.UpsertCourseReviewRequest;
import com.learning.englishpro.course.dto.response.CourseReviewResponse;
import com.learning.englishpro.course.service.CourseReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Course reviews", description = "Đánh giá khóa học (sao + nhận xét)")
@RestController
@RequestMapping("/courses/{courseId}/reviews")
@RequiredArgsConstructor
public class CourseReviewController {

    private final CourseReviewService courseReviewService;

    @Operation(summary = "Danh sách đánh giá theo khóa học [PUBLIC]")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseReviewResponse>>> listReviews(
            @PathVariable Long courseId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<CourseReviewResponse> page = courseReviewService.listReviews(courseId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @Operation(summary = "Đánh giá của tôi cho khóa học (nếu có) [STUDENT]")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseReviewResponse>> getMyReview(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        return courseReviewService.getMyReview(courseId, principal)
                .map(body -> ResponseEntity.ok(ApiResponse.ok(body)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.ok(null)));
    }

    @Operation(summary = "Tạo hoặc cập nhật đánh giá (mỗi học viên một bản ghi) [STUDENT]")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseReviewResponse>> upsertReview(
            @PathVariable Long courseId,
            @Valid @RequestBody UpsertCourseReviewRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        CourseReviewResponse body = courseReviewService.upsertReview(courseId, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Đánh giá đã được lưu", body));
    }

    @Operation(summary = "Xóa đánh giá của chính mình [STUDENT]")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteMyReview(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        courseReviewService.deleteMyReview(courseId, principal);
        return ResponseEntity.ok(ApiResponse.ok("Đã xóa đánh giá", null));
    }
}

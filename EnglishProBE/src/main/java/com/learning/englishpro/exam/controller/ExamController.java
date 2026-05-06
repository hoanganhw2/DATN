package com.learning.englishpro.exam.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.exam.dto.request.CreateExamRequest;
import com.learning.englishpro.exam.dto.request.UpdateExamRequest;
import com.learning.englishpro.exam.dto.response.ExamSummaryResponse;
import com.learning.englishpro.exam.service.ExamService;
import com.learning.englishpro.exam.service.ExamUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Exams", description = "Exam management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final ExamUploadService examUploadService;

    @Operation(summary = "[STUDENT] Get published exams with optional exam type filter and keyword search")
    @GetMapping("/published")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ExamSummaryResponse>>> getPublishedExams(
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getPublishedExams(examType, keyword, pageable)));
    }

    @Operation(summary = "[ADMIN/TEACHER/STUDENT] Get exam by ID with full sections and questions")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<com.learning.englishpro.exam.dto.response.ExamDetailResponse>> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getExamById(id)));
    }

    @Operation(summary = "[ADMIN/TEACHER] Get my created exams")
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Page<ExamSummaryResponse>>> getMyExams(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getMyExams(userDetails.getUsername(), pageable)));
    }

    @Operation(summary = "[ADMIN/TEACHER] Create a new exam")
    @PostMapping({"", "/"})
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamSummaryResponse>> createExam(
            @Valid @RequestBody CreateExamRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(examService.createExam(request, userDetails.getUsername())));
    }

    @Operation(summary = "[ADMIN/TEACHER] Update an exam")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ExamSummaryResponse>> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExamRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Exam updated", examService.updateExam(id, request, userDetails.getUsername(), userDetails.getAuthorities())));
    }

    @Operation(summary = "[ADMIN/TEACHER] Delete an exam")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteExam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        examService.deleteExam(id, userDetails.getUsername(), userDetails.getAuthorities());
        return ResponseEntity.ok(ApiResponse.noContent("Exam deleted"));
    }

    @Operation(summary = "[ADMIN/TEACHER] Upload media file for exam questions")
    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<String>> uploadExamMedia(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok("Media uploaded successfully", examUploadService.uploadMedia(file)));
    }
}

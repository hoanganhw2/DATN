package com.learning.englishpro.exam.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.exam.dto.request.SubmitExamRequest;
import com.learning.englishpro.exam.dto.response.ExamResultDetailResponse;
import com.learning.englishpro.exam.dto.response.ExamResultSummaryResponse;
import com.learning.englishpro.exam.dto.response.StartExamResponse;
import com.learning.englishpro.exam.dto.response.SubmitExamResponse;
import com.learning.englishpro.exam.service.UserExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Exam Taking", description = "Endpoints for students taking exams")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class UserExamController {

    private final UserExamService userExamService;

    @Operation(summary = "[STUDENT] Start or resume an exam")
    @PostMapping("/exams/{examId}/start")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<StartExamResponse>> startExam(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(userExamService.startExam(examId, principal)));
    }

    @Operation(summary = "[STUDENT] Submit exam answers")
    @PostMapping("/exam-results/{examResultId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<SubmitExamResponse>> submitExam(
            @PathVariable Long examResultId,
            @Valid @RequestBody SubmitExamRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok("Exam submitted successfully",
                userExamService.submitExam(examResultId, request, principal)));
    }

    @Operation(summary = "[STUDENT] Get exam result detail")
    @GetMapping("/exam-results/{examResultId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExamResultDetailResponse>> getResultDetail(
            @PathVariable Long examResultId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(userExamService.getResultDetail(examResultId, principal)));
    }

    @Operation(summary = "[STUDENT] Get exam playing history for a specific exam")
    @GetMapping("/exam-results")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ExamResultSummaryResponse>>> getHistory(
            @RequestParam Long examId,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(userExamService.getHistory(examId, pageable, principal)));
    }
}

package com.learning.englishpro.exercise.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.exercise.dto.request.CreateExerciseRequest;
import com.learning.englishpro.exercise.dto.request.SubmitExerciseRequest;
import com.learning.englishpro.exercise.dto.response.ExerciseDetailResponse;
import com.learning.englishpro.exercise.dto.response.ExerciseSummaryResponse;
import com.learning.englishpro.exercise.dto.response.ExerciseSubmitResponse;
import com.learning.englishpro.exercise.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Exercises", description = "Exercise management and submissions")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    // ─────────────────────────────────────────────────────────────────────────
    // STUDENT – Read endpoints
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(summary = "[STUDENT] List exercises for a lesson (no correct answers)")
    @GetMapping("/lessons/{lessonId}/exercises")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ExerciseSummaryResponse>>> listByLesson(
            @PathVariable Long lessonId) {
        return ResponseEntity.ok(
                ApiResponse.ok(exerciseService.listByLesson(lessonId)));
    }

    @Operation(summary = "[STUDENT] Get exercise detail with questions (no correct answers)")
    @GetMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExerciseDetailResponse>> getDetail(
            @PathVariable Long exerciseId) {
        return ResponseEntity.ok(
                ApiResponse.ok(exerciseService.getDetail(exerciseId)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STUDENT – Submit
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(summary = "[STUDENT] Submit answers for an exercise")
    @PostMapping("/exercises/{exerciseId}/submit")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExerciseSubmitResponse>> submit(
            @PathVariable Long exerciseId,
            @Valid @RequestBody SubmitExerciseRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
                ApiResponse.ok("Submitted successfully",
                        exerciseService.submit(exerciseId, request, principal)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TEACHER – CRUD
    // ─────────────────────────────────────────────────────────────────────────

    @Operation(summary = "[TEACHER] Get exercise detail with ALL questions and correct answers")
    @GetMapping("/teacher/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<com.learning.englishpro.exercise.dto.response.TeacherExerciseDetailResponse>> getTeacherDetail(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
                ApiResponse.ok(exerciseService.getTeacherDetail(exerciseId, principal)));
    }


    @Operation(summary = "[TEACHER] Create exercise for a lesson")
    @PostMapping("/lessons/{lessonId}/exercises")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExerciseSummaryResponse>> createExercise(
            @PathVariable Long lessonId,
            @Valid @RequestBody CreateExerciseRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        exerciseService.createExercise(lessonId, request, principal)));
    }

    @Operation(summary = "[TEACHER] Update an exercise (replaces all questions)")
    @PutMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ExerciseSummaryResponse>> updateExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody CreateExerciseRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(
                ApiResponse.ok("Exercise updated",
                        exerciseService.updateExercise(exerciseId, request, principal)));
    }

    @Operation(summary = "[TEACHER] Delete an exercise (cascades to questions and answers)")
    @DeleteMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteExercise(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserDetails principal) {
        exerciseService.deleteExercise(exerciseId, principal);
        return ResponseEntity.ok(ApiResponse.noContent("Exercise deleted"));
    }
}

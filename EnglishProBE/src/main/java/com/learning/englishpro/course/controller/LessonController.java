package com.learning.englishpro.course.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.course.dto.request.LessonRequest;
import com.learning.englishpro.course.dto.response.LessonResponse;
import com.learning.englishpro.course.dto.response.LessonUploadResponse;
import com.learning.englishpro.course.service.LessonService;
import com.learning.englishpro.course.service.LessonUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Lessons", description = "Teacher Lesson Management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final LessonUploadService lessonUploadService;

    @Operation(summary = "[TEACHER] Create new lesson")
    @PostMapping("/courses/{courseId}/chapters/{chapterId}/lessons")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        lessonService.createLesson(principal, courseId, chapterId, request)));
    }

    @Operation(summary = "[TEACHER] Update lesson")
    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity
                .ok(ApiResponse.ok("Lesson updated", lessonService.updateLesson(principal, lessonId, request)));
    }

    @Operation(summary = "[TEACHER] Delete lesson")
    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long lessonId) {
        lessonService.deleteLesson(principal, lessonId);
        return ResponseEntity.ok(ApiResponse.noContent("Lesson deleted"));
    }

    // POST /api/v1/lessons/{lessonId}/upload [TEACHER]
    @Operation(summary = "[TEACHER] Upload media file for a lesson (video/audio)")
    @PostMapping(value = "/lessons/{lessonId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonUploadResponse>> uploadMedia(
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        LessonUploadResponse response = lessonUploadService.uploadMedia(lessonId, file, principal);
        return ResponseEntity.ok(ApiResponse.ok("Media uploaded successfully", response));
    }
}

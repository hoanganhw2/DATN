package com.learning.englishpro.course.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.course.dto.request.ChapterRequest;
import com.learning.englishpro.course.dto.request.ReorderRequest;
import com.learning.englishpro.course.dto.response.ChapterResponse;
import com.learning.englishpro.course.service.ChapterService;
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

@Tag(name = "Chapters", description = "Teacher Chapter Management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/courses/{courseId}/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "[TEACHER] Create new chapter")
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChapterResponse>> createChapter(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long courseId,
            @Valid @RequestBody ChapterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(chapterService.createChapter(principal, courseId, request)));
    }

    @Operation(summary = "[TEACHER] Update chapter")
    @PutMapping("/{chapterId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ChapterResponse>> updateChapter(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long courseId,
            @PathVariable Long chapterId,
            @Valid @RequestBody ChapterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Chapter updated",
                chapterService.updateChapter(principal, courseId, chapterId, request)));
    }

    @Operation(summary = "[TEACHER] Delete chapter")
    @DeleteMapping("/{chapterId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteChapter(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long courseId,
            @PathVariable Long chapterId) {
        chapterService.deleteChapter(principal, courseId, chapterId);
        return ResponseEntity.ok(ApiResponse.noContent("Chapter deleted"));
    }

    @Operation(summary = "[TEACHER] Reorder chapters and lessons")
    @PutMapping("/reorder")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reorder(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long courseId,
            @RequestBody ReorderRequest request) {
        chapterService.reorderCurriculum(principal, courseId, request);
        return ResponseEntity.ok(ApiResponse.noContent("Reorder successful"));
    }
}


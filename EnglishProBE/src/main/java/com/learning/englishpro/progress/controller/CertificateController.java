package com.learning.englishpro.progress.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.progress.dto.response.CertificateResponse;
import com.learning.englishpro.progress.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Certificates", description = "Course completion certificate management")
@RestController
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @Operation(summary = "[STUDENT] Issue a certificate for a completed course")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/certificates/courses/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CertificateResponse>> issueCertificate(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        CertificateResponse response = certificateService.issueCertificate(courseId, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @Operation(summary = "[STUDENT] Get my certificate for a specific course")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/certificates/courses/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CertificateResponse>> getCertificate(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        CertificateResponse response = certificateService.getCertificate(courseId, principal);
        return ResponseEntity.ok(ApiResponse.ok("Certificate retrieved", response));
    }

    @Operation(summary = "[STUDENT] List all my certificates")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/certificates/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getMyCertificates(
            @AuthenticationPrincipal UserDetails principal) {
        List<CertificateResponse> list = certificateService.getMyCertificates(principal);
        return ResponseEntity.ok(ApiResponse.ok("Certificates retrieved", list));
    }

    @Operation(summary = "[PUBLIC] Verify a certificate by its code")
    @GetMapping("/certificates/verify/{code}")
    public ResponseEntity<ApiResponse<CertificateResponse>> verifyCertificate(
            @PathVariable String code) {
        CertificateResponse response = certificateService.verifyCertificate(code);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.ok("Certificate not found", null));
        }
        return ResponseEntity.ok(ApiResponse.ok("Certificate verified successfully", response));
    }
}

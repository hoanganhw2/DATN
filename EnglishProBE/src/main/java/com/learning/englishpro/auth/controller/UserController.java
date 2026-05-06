package com.learning.englishpro.auth.controller;

import com.learning.englishpro.auth.dto.request.UpdateProfileRequest;
import com.learning.englishpro.auth.dto.request.UpdateUserAdminRequest;
import com.learning.englishpro.auth.dto.response.UserDetailResponse;
import com.learning.englishpro.auth.dto.response.UserResponse;
import com.learning.englishpro.auth.service.UserService;
import com.learning.englishpro.common.response.ApiResponse;
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
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Tag(name = "Users", description = "User profile and admin management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── Current user (any authenticated) ────────────────────────────────────

    /** GET /api/v1/users/me */
    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getMe(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMe(principal)));
    }

    /** PATCH /api/v1/users/me */
    @Operation(summary = "Update current user profile (partial)")
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateMe(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", userService.updateMe(principal, request)));
    }

    /** POST /api/v1/users/me/avatar */
    @Operation(summary = "Upload current user avatar")
    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.uploadAvatar(principal, file);
        return ResponseEntity.ok(ApiResponse.ok("Avatar uploaded", Map.of("avatarUrl", avatarUrl)));
    }

    // ── Admin endpoints ──────────────────────────────────────────────────────

    /** GET /api/v1/users?page=0&size=20&sort=createdAt,desc&keyword=john */
    @Operation(summary = "[ADMIN] List all users with pagination")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers(keyword, pageable)));
    }

    /** GET /api/v1/users/stats */
    @Operation(summary = "[ADMIN] Get user distribution stats")
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<com.learning.englishpro.auth.dto.response.UserStatsResponse>> getUserStats() {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserStats()));
    }

    /** GET /api/v1/users/{id} */
    @Operation(summary = "[ADMIN] Get user by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    /** PUT /api/v1/users/{id} */
    @Operation(summary = "[ADMIN] Update user role and status")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> adminUpdate(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserAdminRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("User updated", userService.adminUpdateUser(principal, id, request)));
    }

    /** DELETE /api/v1/users/{id} */
    @Operation(summary = "[ADMIN] Delete a user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        userService.deleteUser(principal, id);
        return ResponseEntity.ok(ApiResponse.noContent("Đã xóa người dùng"));
    }
}

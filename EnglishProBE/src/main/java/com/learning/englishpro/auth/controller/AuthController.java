package com.learning.englishpro.auth.controller;

import com.learning.englishpro.auth.dto.request.LoginRequest;
import com.learning.englishpro.auth.dto.request.RefreshTokenRequest;
import com.learning.englishpro.auth.dto.request.RegisterRequest;
import com.learning.englishpro.auth.dto.response.AuthResponse;
import com.learning.englishpro.auth.dto.response.UserResponse;
import com.learning.englishpro.auth.service.AuthService;
import com.learning.englishpro.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Register, Login, Token refresh and Logout")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** POST /api/v1/auth/register */
    @Operation(summary = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(user));
    }

    /** POST /api/v1/auth/login */
    @Operation(summary = "Login and receive JWT tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(request)));
    }

    /** POST /api/v1/auth/refresh-token */
    @Operation(summary = "Exchange a refresh token for a new token pair")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", authService.refreshToken(request)));
    }

    /** POST /api/v1/auth/logout  (requires valid access token) */
    @Operation(summary = "Logout — revoke the refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(ApiResponse.noContent("Logged out successfully"));
    }
}

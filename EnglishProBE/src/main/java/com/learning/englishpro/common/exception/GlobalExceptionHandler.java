package com.learning.englishpro.common.exception;

import com.learning.englishpro.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 1. Domain / business errors (AppException) ─────────────────────────
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode code = ex.getErrorCode();
        log.warn("Ngoại lệ AppException: [{}] {}", code.name(), code.getMessage());
        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.<Void>builder()
                        .status(code.getCode())
                        .message(code.getMessage())
                        .build());
    }

    // ── 2. Bean-validation errors (@Valid) ─────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .status(400)
                        .message("Validation failed")
                        .data(errors)
                        .build());
    }

    // ── 3. Spring Security: forbidden ──────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        ErrorCode code = ErrorCode.ACCESS_DENIED;
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .status(code.getCode())
                        .message(code.getMessage())
                        .build());
    }

    // ── 4. Spring Security: unauthenticated ────────────────────────────────
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        ErrorCode code = ErrorCode.UNAUTHENTICATED;
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .status(code.getCode())
                        .message(ex.getMessage())
                        .build());
    }

    // ── 5. Multipart / file-size exceeded ─────────────────────────────────
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.warn("Kích thước file vượt quá giới hạn: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONTENT_TOO_LARGE)
                .body(ApiResponse.<Void>builder()
                        .status(413)
                        .message("Uploaded file exceeds the maximum allowed size")
                        .build());
    }

    // ── 6. Client disconnected during media streaming (audio/video seek, navigate away, etc.) ─
    // This is EXPECTED behavior – do NOT log as error and do NOT try to write a response
    // because the connection is already closed.
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException ex, HttpServletResponse response) {
        // Connection was closed by the browser – silently ignore
        log.debug("Client disconnected during media streaming (expected): {}", ex.getMessage());
    }

    // ── 7. Fallback ────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Lỗi không xác định", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Void>builder()
                        .status(500)
                        .message("An unexpected error occurred")
                        .build());
    }
}

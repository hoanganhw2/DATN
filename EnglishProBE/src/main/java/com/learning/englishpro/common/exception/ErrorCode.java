package com.learning.englishpro.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ── Auth ──────────────────────────────────────────────────────────────
    EMAIL_ALREADY_EXISTS(400, "Email đã được đăng ký", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(400, "Tên đăng nhập đã được sử dụng", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(401, "Email hoặc mật khẩu không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(401, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND(404, "Không tìm thấy Refresh token", HttpStatus.NOT_FOUND),
    TOKEN_EXPIRED(401, "Refresh token đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(401, "Refresh token đã bị thu hồi", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(401, "Yêu cầu xác thực", HttpStatus.UNAUTHORIZED),

    // ── User ──────────────────────────────────────────────────────────────
    USER_NOT_FOUND(404, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    ACCESS_DENIED(403, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    CANNOT_MODIFY_SELF(400, "Quản trị viên không thể tự sửa hoặc xóa chính mình", HttpStatus.BAD_REQUEST),

    // ── Course ────────────────────────────────────────────────────────────
    COURSE_NOT_FOUND(404, "Không tìm thấy khóa học", HttpStatus.NOT_FOUND),
    COURSE_REVIEW_NOT_FOUND(404, "Bạn chưa có đánh giá cho khóa học này", HttpStatus.NOT_FOUND),
    TEACHER_CANNOT_REVIEW_OWN_COURSE(403, "Giảng viên không thể đánh giá khóa học của chính mình", HttpStatus.FORBIDDEN),
    CHAPTER_NOT_FOUND(404, "Không tìm thấy chương học", HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(404, "Không tìm thấy bài học", HttpStatus.NOT_FOUND),
    COURSE_HAS_ENROLLMENTS(400, "Không thể xóa khóa học đã có học viên đăng ký", HttpStatus.BAD_REQUEST),

    // ── Exercise ──────────────────────────────────────────────────────────
    EXERCISE_NOT_FOUND(404, "Không tìm thấy bài tập", HttpStatus.NOT_FOUND),
    EXERCISE_QUESTION_NOT_FOUND(404, "Không tìm thấy câu hỏi bài tập", HttpStatus.NOT_FOUND),
    EXERCISE_NOT_IN_LESSON(400, "Bài tập không thuộc bài học đã chỉ định", HttpStatus.BAD_REQUEST),

    // ── Exam ──────────────────────────────────────────────────────────────
    EXAM_NOT_FOUND(404, "Không tìm thấy đề thi", HttpStatus.NOT_FOUND),
    EXAM_NOT_PUBLISHED(403, "Đề thi chưa được xuất bản", HttpStatus.FORBIDDEN),
    EXAM_MAX_ATTEMPTS_EXCEEDED(400, "Bạn đã hết lượt thi cho đề thi này", HttpStatus.BAD_REQUEST),
    EXAM_RESULT_NOT_FOUND(404, "Không tìm thấy kết quả thi", HttpStatus.NOT_FOUND),
    EXAM_ALREADY_SUBMITTED(409, "Đề thi đã được nộp", HttpStatus.CONFLICT),

    // ── Flashcard ─────────────────────────────────────────────────────────
    FLASHCARD_DECK_NOT_FOUND(404, "Không tìm thấy bộ thẻ ghi nhớ", HttpStatus.NOT_FOUND),
    FLASHCARD_NOT_FOUND(404, "Không tìm thấy thẻ ghi nhớ", HttpStatus.NOT_FOUND),
    FLASHCARD_DECK_FULL(400, "Bộ thẻ đã đạt giới hạn tối đa 50 thẻ. Vui lòng tạo bộ thẻ mới để tiếp tục.", HttpStatus.BAD_REQUEST),

    // ── Enrollment ────────────────────────────────────────────────────────
    ALREADY_ENROLLED(409, "Bạn đã đăng ký khóa học này", HttpStatus.CONFLICT),
    PAYMENT_REQUIRED(403, "Yêu cầu thanh toán để đăng ký khóa học này", HttpStatus.FORBIDDEN),
    COURSE_ALREADY_PURCHASED(409, "Bạn đã mua khóa học này rồi", HttpStatus.CONFLICT),
    NOT_ENROLLED(403, "Bạn chưa đăng ký khóa học này", HttpStatus.FORBIDDEN),
    COURSE_NOT_COMPLETED(400, "Bạn chưa hoàn thành 100% khóa học này để nhận chứng chỉ", HttpStatus.BAD_REQUEST),

    // ── Order ────────────────────────────────────────────────────────────
    ORDER_NOT_FOUND(404, "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    ORDER_CANNOT_BE_CANCELLED(400, "Không thể hủy đơn hàng này", HttpStatus.BAD_REQUEST),

    // ── File Upload ───────────────────────────────────────────────────────
    FILE_TOO_LARGE(400, "Kích thước file vượt quá giới hạn cho phép", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE_THUMBNAIL(400, "Ảnh thu nhỏ phải ≤ 10 MB", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE_VIDEO(413, "File video phải ≤ 2 GB", HttpStatus.CONTENT_TOO_LARGE),
    FILE_TOO_LARGE_AUDIO(400, "File âm thanh phải ≤ 100 MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(400, "Loại file không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(500, "Tải file lên thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    MEDIA_PARSE_FAILED(500, "Không thể đọc metadata của file đa phương tiện", HttpStatus.INTERNAL_SERVER_ERROR),

    // ── Coupon ────────────────────────────────────────────────────────────
    COUPON_NOT_FOUND(404, "Không tìm thấy mã giảm giá", HttpStatus.NOT_FOUND),
    COUPON_INVALID(400, "Mã giảm giá không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    COUPON_ALREADY_EXISTS(409, "Mã giảm giá đã tồn tại", HttpStatus.CONFLICT),
    COUPON_NOT_APPLICABLE(400, "Mã giảm giá không áp dụng cho khóa học này", HttpStatus.BAD_REQUEST),

    // ── Generic ───────────────────────────────────────────────────────────
    RESOURCE_NOT_FOUND(404, "Không tìm thấy tài nguyên", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS(403, "Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
    VALIDATION_FAILED(400, "Lỗi xác thực dữ liệu", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(500, "Đã xảy ra lỗi không xác định", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

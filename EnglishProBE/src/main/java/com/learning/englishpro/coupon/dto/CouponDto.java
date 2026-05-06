package com.learning.englishpro.coupon.dto;

import com.learning.englishpro.coupon.entity.CouponScope;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CouponDto {

    @Data
    @Builder
    public static class Response {
        private Long id;
        private String code;
        private Integer discountPercent;
        private Boolean isActive;
        private Instant startDate;
        private Instant endDate;
        private Integer usageLimit;
        private Integer usedCount;
        private boolean valid;
        private CouponScope scope;
        private Long createdById;
        private String createdByUsername;
        /** Tên các khóa học được áp dụng (scope=SPECIFIC) */
        private List<Long> courseIds;
        private List<String> courseNames;
    }

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Mã giảm giá không được để trống")
        private String code;

        @NotNull(message = "Phần trăm giảm giá không được để trống")
        @Min(value = 1, message = "Tối thiểu 1%")
        @Max(value = 100, message = "Tối đa 100%")
        private Integer discountPercent;

        private Boolean isActive = true;
        private Instant startDate;
        private Instant endDate;
        private Integer usageLimit;

        /**
         * GLOBAL (Admin only) hoặc SPECIFIC (Teacher).
         * Mặc định GLOBAL nếu do Admin tạo, SPECIFIC nếu do Teacher tạo.
         */
        private CouponScope scope;

        /** Danh sách courseId áp dụng (scope=SPECIFIC, rỗng = toàn bộ khóa của teacher) */
        private List<Long> courseIds = new ArrayList<>();
    }

    @Data
    public static class UpdateRequest {
        @Min(value = 1, message = "Tối thiểu 1%")
        @Max(value = 100, message = "Tối đa 100%")
        private Integer discountPercent;

        private Boolean isActive;
        private Instant startDate;
        private Instant endDate;
        private Integer usageLimit;
        private List<Long> courseIds;
    }
}

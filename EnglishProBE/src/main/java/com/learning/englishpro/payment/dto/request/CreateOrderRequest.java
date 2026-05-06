package com.learning.englishpro.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateOrderRequest(
        @NotNull(message = "Mã khóa học không được để trống")
        Long courseId,

        @NotBlank(message = "Phương thức thanh toán không được để trống")
        @Pattern(regexp = "VNPAY|MOMO|PAYPAL|CREDIT_CARD", message = "Phương thức thanh toán phải là VNPAY, MOMO, PAYPAL, hoặc CREDIT_CARD")
        String paymentMethod,
        
        String couponCode
) {}

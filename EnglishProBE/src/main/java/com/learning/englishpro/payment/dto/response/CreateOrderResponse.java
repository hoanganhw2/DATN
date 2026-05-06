package com.learning.englishpro.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateOrderResponse(
        Long orderId,
        String orderCode,
        Long courseId,
        String courseTitle,
        BigDecimal amount,
        BigDecimal finalAmount,
        String status,
        String paymentUrl,
        Instant expiredAt
) {}

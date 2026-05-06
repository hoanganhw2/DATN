package com.learning.englishpro.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.learning.englishpro.payment.entity.Order;
import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        Long id,
        String orderCode,
        String username,
        String fullName,
        Long courseId,
        String courseTitle,
        String courseThumbnail,
        BigDecimal amount,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String status,
        Instant expiredAt,
        Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getUser().getUsername(),
                order.getUser().getProfile() != null ? order.getUser().getProfile().getFullName() : null,
                order.getCourse().getId(),
                order.getCourse().getTitle(),
                order.getCourse().getThumbnailUrl(),
                order.getAmount(),
                order.getDiscountAmount(),
                order.getFinalAmount(),
                order.getStatus().name(),
                order.getExpiredAt(),
                order.getCreatedAt()
        );
    }
}

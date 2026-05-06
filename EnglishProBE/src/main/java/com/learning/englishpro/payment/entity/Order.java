package com.learning.englishpro.payment.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.common.entity.BaseAuditEntity;
import com.learning.englishpro.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_user_id",    columnList = "user_id"),
        @Index(name = "idx_orders_course_id",  columnList = "course_id"),
        @Index(name = "idx_orders_order_code", columnList = "order_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Giá gốc của khoá học tại thời điểm đặt */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** Số tiền giảm giá */
    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /** Số tiền thực thu = amount - discountAmount */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal finalAmount;

    /** Mã giảm giá đã áp dụng (nullable) */
    @Column(length = 50)
    private String couponCode;

    /**
     * Doanh thu quy về cho giảng viên.
     * - Nếu không dùng coupon hoặc dùng coupon SPECIFIC (teacher tạo): bằng finalAmount.
     * - Nếu dùng coupon GLOBAL (admin tạo): bằng amount (giá gốc) — phần giảm do nền tảng chịu.
     */
    @Column(precision = 12, scale = 2)
    private BigDecimal teacherRevenue;

    /** Loại tiền tệ (mặc định VND) */
    @Column(length = 10, nullable = false)
    @Builder.Default
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(unique = true, nullable = false, length = 100)
    private String orderCode;

    /** Thời hạn thanh toán — link VNPay/MoMo thường có timeout 15 phút */
    private Instant expiredAt;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

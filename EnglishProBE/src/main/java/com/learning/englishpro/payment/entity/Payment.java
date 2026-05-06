package com.learning.englishpro.payment.entity;

import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_order_id",       columnList = "order_id"),
        @Index(name = "idx_payments_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_payments_gateway_ref",    columnList = "gateway_ref")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Payment extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /** Mã giao dịch nội bộ (sinh bởi hệ thống) */
    @Column(unique = true, nullable = false, length = 200)
    private String transactionId;

    /** Mã tham chiếu phía payment gateway (VNPay: vnp_TxnRef, MoMo: requestId) */
    @Column(length = 200)
    private String gatewayRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** Loại tiền tệ */
    @Column(length = 10, nullable = false)
    @Builder.Default
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    private Instant paidAt;

    /** Raw response từ gateway — lưu để debug và đối soát */
    @Column(columnDefinition = "JSON")
    private String paymentData;
}

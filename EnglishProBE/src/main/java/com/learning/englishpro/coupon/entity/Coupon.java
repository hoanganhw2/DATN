package com.learning.englishpro.coupon.entity;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Coupon extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    /** Phần trăm giảm giá (VD: 20 là 20%) */
    @Column(nullable = false)
    private Integer discountPercent;

    /** Trạng thái kích hoạt tổng thể */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private Instant startDate;
    private Instant endDate;

    /** Giới hạn số lượng dùng. Null = không giới hạn */
    private Integer usageLimit;

    /** Số lượng đã dùng */
    @Column(nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    /**
     * Phạm vi áp dụng.
     * GLOBAL  = Admin tạo, áp dụng tất cả khóa học — nền tảng chịu phần giảm, doanh thu teacher tính giá gốc.
     * SPECIFIC = Teacher tạo, chỉ áp dụng cho các khóa của mình — teacher chịu phần giảm.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CouponScope scope = CouponScope.GLOBAL;

    /**
     * Người tạo mã (Admin hoặc Teacher).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    /**
     * Danh sách khóa học được áp dụng (chỉ dùng khi scope = SPECIFIC).
     * Nếu danh sách rỗng và scope=SPECIFIC thì áp dụng cho tất cả khóa của teacher đó.
     */
    @ElementCollection
    @CollectionTable(name = "coupon_courses", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "course_id")
    @Builder.Default
    private List<Long> courseIds = new ArrayList<>();

    public boolean isValid() {
        if (!isActive) return false;
        Instant now = Instant.now();
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        if (usageLimit != null && usedCount >= usageLimit) return false;
        return true;
    }

    /**
     * Kiểm tra coupon có áp dụng được cho courseId và teacherId không.
     */
    public boolean isApplicableTo(Long courseId, Long courseTeacherId) {
        if (scope == CouponScope.GLOBAL) return true;
        // SPECIFIC: chỉ teacher tạo ra coupon mới áp dụng được cho khóa học của mình
        if (createdBy == null || !createdBy.getId().equals(courseTeacherId)) return false;
        return courseIds.isEmpty() || courseIds.contains(courseId);
    }
}

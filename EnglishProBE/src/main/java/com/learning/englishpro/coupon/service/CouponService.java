package com.learning.englishpro.coupon.service;

import com.learning.englishpro.auth.entity.Role;
import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.coupon.dto.CouponDto;
import com.learning.englishpro.coupon.entity.Coupon;
import com.learning.englishpro.coupon.entity.CouponScope;
import com.learning.englishpro.coupon.repository.CouponRepository;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    // ─── List ──────────────────────────────────────────────────────────────────

    /**
     * Admin: xem / tìm kiếm tất cả coupon (lọc theo keyword, scope).
     * Teacher: xem / tìm kiếm coupon của mình (lọc theo keyword).
     *
     * @param keyword tìm theo mã coupon (không phân biệt hoa thường), null = không lọc
     * @param scope   lọc theo phạm vi (GLOBAL / SPECIFIC), null = tất cả
     */
    @Transactional(readOnly = true)
    public Page<CouponDto.Response> getCoupons(UserDetails principal,
                                               String keyword,
                                               CouponScope scope,
                                               Pageable pageable) {
        User caller = resolveUser(principal);
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;
        if (caller.getRole() == Role.ADMIN) {
            return couponRepository.searchAll(kw, scope, pageable).map(this::mapToResponse);
        }
        // Teacher: chỉ tìm trong coupon của mình
        return couponRepository.searchByCreatedById(caller.getId(), kw, pageable).map(this::mapToResponse);
    }

    // ─── Validate (public) ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public CouponDto.Response validateCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
        if (!coupon.isValid()) {
            throw new AppException(ErrorCode.COUPON_INVALID);
        }
        return mapToResponse(coupon);
    }

    // ─── Create ────────────────────────────────────────────────────────────────

    public CouponDto.Response createCoupon(CouponDto.CreateRequest request, UserDetails principal) {
        User caller = resolveUser(principal);

        if (couponRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new AppException(ErrorCode.COUPON_ALREADY_EXISTS);
        }

        // Xác định scope cứng theo role: Admin → GLOBAL, Teacher → SPECIFIC
        CouponScope scope = (caller.getRole() == Role.ADMIN) ? CouponScope.GLOBAL : CouponScope.SPECIFIC;

        // Teacher: kiểm tra danh sách courseIds phải thuộc về mình
        List<Long> courseIds = request.getCourseIds() != null ? request.getCourseIds() : new ArrayList<>();
        if (scope == CouponScope.SPECIFIC && caller.getRole() == Role.TEACHER && !courseIds.isEmpty()) {
            for (Long courseId : courseIds) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
                if (!course.getTeacher().getId().equals(caller.getId())) {
                    throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
                }
            }
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .discountPercent(request.getDiscountPercent())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .usedCount(0)
                .scope(scope)
                .createdBy(caller)
                .courseIds(courseIds)
                .build();

        return mapToResponse(couponRepository.save(coupon));
    }

    // ─── Update ────────────────────────────────────────────────────────────────

    public CouponDto.Response updateCoupon(Long id, CouponDto.UpdateRequest request, UserDetails principal) {
        User caller = resolveUser(principal);
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        // Teacher chỉ sửa coupon của mình
        if (caller.getRole() == Role.TEACHER) {
            if (coupon.getCreatedBy() == null || !coupon.getCreatedBy().getId().equals(caller.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }

        if (request.getDiscountPercent() != null) coupon.setDiscountPercent(request.getDiscountPercent());
        if (request.getIsActive() != null) coupon.setIsActive(request.getIsActive());
        if (request.getStartDate() != null) coupon.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) coupon.setEndDate(request.getEndDate());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getCourseIds() != null) {
            // Validate ownership if teacher
            if (caller.getRole() == Role.TEACHER) {
                for (Long courseId : request.getCourseIds()) {
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
                    if (!course.getTeacher().getId().equals(caller.getId())) {
                        throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
                    }
                }
            }
            coupon.setCourseIds(request.getCourseIds());
        }

        return mapToResponse(couponRepository.save(coupon));
    }

    // ─── Delete ────────────────────────────────────────────────────────────────

    public void deleteCoupon(Long id, UserDetails principal) {
        User caller = resolveUser(principal);
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        if (caller.getRole() == Role.TEACHER) {
            if (coupon.getCreatedBy() == null || !coupon.getCreatedBy().getId().equals(caller.getId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
            }
        }
        couponRepository.delete(coupon);
    }

    // ─── Internal helpers ──────────────────────────────────────────────────────

    private User resolveUser(UserDetails principal) {
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    private CouponDto.Response mapToResponse(Coupon coupon) {
        // Lấy danh sách tên khóa học nếu có
        List<String> courseNames = new ArrayList<>();
        if (coupon.getCourseIds() != null && !coupon.getCourseIds().isEmpty()) {
            for (Long courseId : coupon.getCourseIds()) {
                courseRepository.findById(courseId).ifPresent(c -> courseNames.add(c.getTitle()));
            }
        }

        return CouponDto.Response.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountPercent(coupon.getDiscountPercent())
                .isActive(coupon.getIsActive())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .usageLimit(coupon.getUsageLimit())
                .usedCount(coupon.getUsedCount())
                .valid(coupon.isValid())
                .scope(coupon.getScope())
                .createdById(coupon.getCreatedBy() != null ? coupon.getCreatedBy().getId() : null)
                .createdByUsername(coupon.getCreatedBy() != null ? coupon.getCreatedBy().getUsername() : null)
                .courseIds(coupon.getCourseIds())
                .courseNames(courseNames)
                .build();
    }
}

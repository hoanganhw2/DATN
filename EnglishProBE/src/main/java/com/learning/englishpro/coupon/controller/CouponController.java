package com.learning.englishpro.coupon.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.coupon.dto.CouponDto;
import com.learning.englishpro.coupon.entity.CouponScope;
import com.learning.englishpro.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /** GET /coupons — Admin xem tất cả, Teacher xem của mình; có thể lọc theo keyword + scope */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Page<CouponDto.Response>>> getCoupons(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CouponScope scope,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                couponService.getCoupons(principal, keyword, scope, pageable)));
    }

    /** GET /coupons/validate/{code} — Học viên validate mã lúc mua hàng (public) */
    @GetMapping("/validate/{code}")
    public ResponseEntity<ApiResponse<CouponDto.Response>> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(couponService.validateCoupon(code)));
    }

    /** POST /coupons — Admin tạo GLOBAL hoặc SPECIFIC; Teacher chỉ tạo SPECIFIC */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CouponDto.Response>> createCoupon(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CouponDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(couponService.createCoupon(request, principal)));
    }

    /** PUT /coupons/{id} — Sửa; Teacher chỉ sửa của mình */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CouponDto.Response>> updateCoupon(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id,
            @Valid @RequestBody CouponDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(couponService.updateCoupon(id, request, principal)));
    }

    /** DELETE /coupons/{id} — Xóa; Teacher chỉ xóa của mình */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id) {
        couponService.deleteCoupon(id, principal);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}

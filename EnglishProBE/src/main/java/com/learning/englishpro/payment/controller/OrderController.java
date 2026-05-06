package com.learning.englishpro.payment.controller;

import com.learning.englishpro.common.response.ApiResponse;
import com.learning.englishpro.payment.dto.request.CreateOrderRequest;
import com.learning.englishpro.payment.dto.response.CreateOrderResponse;
import com.learning.englishpro.payment.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.learning.englishpro.payment.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "Orders & Payments", description = "Checkout flow and payment integration")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "[STUDENT] Create a new order and get payment URL")
    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(orderService.createOrder(request, principal)));
    }

    @Operation(summary = "[USER] Get my order history")
    @GetMapping("/orders/me")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            Pageable pageable,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getMyOrders(principal, pageable)));
    }

    @Operation(summary = "[ADMIN] Get all orders")
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getAllOrders(pageable)));
    }

    @Operation(summary = "[USER] Cancel an order that is pending payment")
    @PutMapping("/orders/{id}/cancel")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelMyOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        orderService.cancelMyOrder(id, principal);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "[USER] Re-initiate payment for a PENDING order (returns a new VNPay URL)")
    @GetMapping("/orders/{id}/repay")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> repayOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {
        String paymentUrl = orderService.repayOrder(id, principal);
        return ResponseEntity.ok(ApiResponse.ok(paymentUrl));
    }

    @Operation(summary = "[USER] Check if current user has purchased a specific course")
    @GetMapping("/orders/me/purchased/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<java.util.Map<String, Boolean>>> checkCoursePurchased(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails principal) {
        boolean purchased = orderService.isCoursePurchased(courseId, principal);
        return ResponseEntity.ok(ApiResponse.ok(java.util.Map.of("purchased", purchased)));
    }
}

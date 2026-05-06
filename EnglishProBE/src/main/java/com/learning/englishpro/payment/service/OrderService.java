package com.learning.englishpro.payment.service;

import com.learning.englishpro.auth.entity.User;
import com.learning.englishpro.auth.repository.UserRepository;
import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.course.entity.Course;
import com.learning.englishpro.course.repository.CourseRepository;
import com.learning.englishpro.payment.dto.request.CreateOrderRequest;
import com.learning.englishpro.payment.dto.response.CreateOrderResponse;
import com.learning.englishpro.payment.dto.response.OrderResponse;
import com.learning.englishpro.payment.entity.Order;
import com.learning.englishpro.payment.entity.OrderStatus;
import com.learning.englishpro.payment.entity.Payment;
import com.learning.englishpro.payment.entity.PaymentMethod;
import com.learning.englishpro.coupon.entity.Coupon;
import com.learning.englishpro.coupon.repository.CouponRepository;
import com.learning.englishpro.payment.entity.PaymentStatus;
import com.learning.englishpro.payment.repository.OrderRepository;
import com.learning.englishpro.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final CouponRepository couponRepository;

    public CreateOrderResponse createOrder(CreateOrderRequest request, UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!course.getIsPublished()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND); 
        }

        boolean alreadyPurchased = orderRepository.existsByUserIdAndCourseIdAndStatus(user.getId(), course.getId(), OrderStatus.COMPLETED);
        if (alreadyPurchased) {
            throw new AppException(ErrorCode.COURSE_ALREADY_PURCHASED);
        }

        BigDecimal originalAmount = course.getPrice() != null ? course.getPrice() : BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalAmount = originalAmount;
        BigDecimal teacherRevenue = originalAmount; // default: teacher gets full original price

        if (request.couponCode() != null && !request.couponCode().isBlank()) {
            com.learning.englishpro.coupon.entity.Coupon coupon = couponRepository
                    .findByCode(request.couponCode().toUpperCase())
                    .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

            if (!coupon.isValid()) {
                throw new AppException(ErrorCode.COUPON_INVALID);
            }

            // Check if coupon is applicable to this course
            Long teacherId = course.getTeacher() != null ? course.getTeacher().getId() : null;
            if (!coupon.isApplicableTo(course.getId(), teacherId)) {
                throw new AppException(ErrorCode.COUPON_NOT_APPLICABLE);
            }

            // Calculate discount
            BigDecimal discountPercent = new BigDecimal(coupon.getDiscountPercent());
            discountAmount = originalAmount.multiply(discountPercent).divide(new BigDecimal(100));
            finalAmount = originalAmount.subtract(discountAmount);

            // Teacher revenue logic:
            // - GLOBAL (Admin tạo): Admin chịu phần giảm → teacher nhận giá gốc.
            // - SPECIFIC (Teacher tạo): Cả hai chia theo giá sau giảm (finalAmount).
            if (coupon.getScope() == com.learning.englishpro.coupon.entity.CouponScope.GLOBAL) {
                teacherRevenue = originalAmount;
            } else {
                teacherRevenue = finalAmount;
            }

            // Update used count
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        Order order = Order.builder()
                .user(user)
                .course(course)
                .amount(originalAmount)
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .teacherRevenue(teacherRevenue)
                .couponCode(request.couponCode() != null ? request.couponCode().toUpperCase() : null)
                .status(OrderStatus.PENDING)
                .orderCode("TMP") // Temporary
                .expiredAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();

        Order savedOrder = orderRepository.save(order);
        
        // EP-YYYYMMDD-0000X
        String orderCode = "EP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + String.format("%05d", savedOrder.getId());
        savedOrder.setOrderCode(orderCode);
        orderRepository.save(savedOrder);
        
        Payment payment = Payment.builder()
                .order(savedOrder)
                .transactionId(java.util.UUID.randomUUID().toString())
                .paymentMethod(PaymentMethod.valueOf(request.paymentMethod().toUpperCase()))
                .amount(savedOrder.getFinalAmount())
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        String paymentUrl = paymentGatewayService.createPaymentUrl(savedOrder);

        return new CreateOrderResponse(
                savedOrder.getId(),
                savedOrder.getOrderCode(),
                course.getId(),
                course.getTitle(),
                savedOrder.getAmount(),
                savedOrder.getFinalAmount(),
                savedOrder.getStatus().name(),
                paymentUrl,
                savedOrder.getExpiredAt()
        );
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderResponse> getMyOrders(UserDetails principal, org.springframework.data.domain.Pageable pageable) {
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderResponse> getAllOrders(org.springframework.data.domain.Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(OrderResponse::from);
    }

    @Transactional
    public void cancelMyOrder(Long orderId, UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }

        order.setStatus(OrderStatus.CANCELLED);
        if (order.getCouponCode() != null && !order.getCouponCode().isBlank()) {
            couponRepository.findByCode(order.getCouponCode()).ifPresent(coupon -> {
                coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1));
                couponRepository.save(coupon);
            });
        }
        orderRepository.save(order);

        paymentRepository.findByOrder(order).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
    }

    /**
     * Kiểm tra xem người dùng hiện tại đã mua (COMPLETED) khóa học hay chưa.
     */
    @Transactional(readOnly = true)
    public boolean isCoursePurchased(Long courseId, UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return orderRepository.existsByUserIdAndCourseIdAndStatus(user.getId(), courseId, OrderStatus.COMPLETED);
    }

    /**
     * Tạo lại payment URL cho đơn hàng đang PENDING.
     * Gia hạn thêm 15 phút kể từ thời điểm gọi.
     */
    @Transactional
    public String repayOrder(Long orderId, UserDetails principal) {
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED); // Đơn không còn ở PENDING
        }

        // Gia hạn thêm 15 phút
        order.setExpiredAt(Instant.now().plus(15, ChronoUnit.MINUTES));
        orderRepository.save(order);

        return paymentGatewayService.createPaymentUrl(order);
    }
}

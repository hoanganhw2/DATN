package com.learning.englishpro.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.englishpro.payment.dto.response.VnPayIpnResponse;
import com.learning.englishpro.course.repository.CourseRepository;

import com.learning.englishpro.payment.entity.*;
import com.learning.englishpro.payment.repository.OrderRepository;
import com.learning.englishpro.payment.repository.PaymentRepository;
import com.learning.englishpro.payment.util.VNPaySignatureUtil;
import com.learning.englishpro.progress.entity.EnrollmentStatus;
import com.learning.englishpro.progress.entity.UserCourse;
import com.learning.englishpro.progress.repository.UserCourseRepository;
import com.learning.englishpro.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserCourseRepository userCourseRepository;
    private final CourseRepository courseRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Value("${payment.vnpay.secret-key:fake-secret-key}")
    private String vnpHashSecret;

    @Transactional
    public VnPayIpnResponse processIpn(Map<String, String> requestParams) {
        log.info("Đã nhận VNPay IPN: {}", requestParams);

        // 1. Lấy thông tin secure hash
        String vnp_SecureHash = requestParams.get("vnp_SecureHash");

        // Loại bỏ secure hash để tạo chuỗi dữ liệu ký
        Map<String, String> signParams = new HashMap<>(requestParams);
        signParams.remove("vnp_SecureHashType");
        signParams.remove("vnp_SecureHash");

        // 2. Verify chữ ký
        String signValue = VNPaySignatureUtil.hashAllFields(signParams, vnpHashSecret);
        if (!signValue.equals(vnp_SecureHash)) {
            log.error("Chữ ký không hợp lệ");
            return new VnPayIpnResponse("97", "Invalid Signature");
        }

        String orderCode = requestParams.get("vnp_TxnRef");
        String responseCode = requestParams.get("vnp_ResponseCode");
        String transactionNo = requestParams.get("vnp_TransactionNo");
        String amountStr = requestParams.get("vnp_Amount");

        // 3. Tìm Order
        Order order = orderRepository.findByOrderCode(orderCode).orElse(null);
        if (order == null) {
            log.error("Không tìm thấy đơn hàng: {}", orderCode);
            return new VnPayIpnResponse("01", "Order Not Found");
        }

        // 4. Nếu đã hoàn thành -> idempotent
        if (order.getStatus() == OrderStatus.COMPLETED) {
            log.info("Đơn hàng đã hoàn thành trước đó: {}", orderCode);
            return new VnPayIpnResponse("00", "Confirm Success"); // Already updated
        }

        // 5. Kiểm tra Amount
        try {
            long reqAmount = Long.parseLong(amountStr) / 100;
            if (order.getFinalAmount().compareTo(BigDecimal.valueOf(reqAmount)) != 0) {
                log.error("Số tiền không hợp lệ: mong chờ {}, nhận được {}", order.getFinalAmount(), reqAmount);
                return new VnPayIpnResponse("04", "Invalid Amount");
            }
        } catch (NumberFormatException e) {
            return new VnPayIpnResponse("04", "Invalid Amount Format");
        }

        // Parse toàn bộ dữ liệu payment (JSON)
        String paymentDataRaw = "{}";
        try {
            paymentDataRaw = objectMapper.writeValueAsString(requestParams);
        } catch (Exception e) {
            log.error("Không thể phân tích dữ liệu thanh toán sang JSON", e);
        }

        // 6. Xử lý trạng thái giao dịch
        if ("00".equals(responseCode)) { // Thành công
            // Update Order
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // Update Payment PENDING hiện tại (đã được tạo khi createOrder)
            // hoặc tạo mới nếu không tìm thấy (safety fallback)
            Payment payment = paymentRepository.findByOrder(order)
                    .orElseGet(() -> Payment.builder()
                            .order(order)
                            .paymentMethod(PaymentMethod.VNPAY)
                            .amount(order.getFinalAmount())
                            .build());
            payment.setTransactionId(transactionNo);
            payment.setPaymentMethod(PaymentMethod.VNPAY);
            payment.setAmount(order.getFinalAmount());
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(Instant.now());
            payment.setPaymentData(paymentDataRaw);
            paymentRepository.save(payment);

            // Insert UserCourse nếu chưa tồn tại
            UserCourse.UserCourseId userCourseId = new UserCourse.UserCourseId(order.getUser().getId(),
                    order.getCourse().getId());
            Optional<UserCourse> existingUc = userCourseRepository.findById(userCourseId);
            if (existingUc.isEmpty()) {
                UserCourse userCourse = UserCourse.builder()
                        .user(order.getUser())
                        .course(order.getCourse())
                        .enrolledAt(Instant.now())
                        .progressPercent(BigDecimal.ZERO)
                        .status(EnrollmentStatus.IN_PROGRESS)
                        .build();
                userCourseRepository.save(userCourse);
            }

            long enrollments = userCourseRepository.countByCourseId(order.getCourse().getId());
            order.getCourse().setTotalEnrollments((int) enrollments);
            courseRepository.save(order.getCourse());

            // Gửi email hóa đơn điện tử (async - truyền id để tránh
            // LazyInitializationException)
            try {
                emailService.sendOrderInvoiceEmail(order.getId());
            } catch (Exception e) {
                log.error("Lỗi khi gửi email xác nhận cho đơn {}: {}", order.getOrderCode(), e.getMessage());
            }

        } else { // Thất bại
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);

            // Update Payment PENDING hiện tại
            Payment payment = paymentRepository.findByOrder(order)
                    .orElseGet(() -> Payment.builder()
                            .order(order)
                            .paymentMethod(PaymentMethod.VNPAY)
                            .amount(order.getFinalAmount())
                            .build());
            payment.setTransactionId(transactionNo);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setPaymentData(paymentDataRaw);
            paymentRepository.save(payment);
        }

        // 8. Trả về thành công nghiệp vụ (luôn return 200 {00, Confirm Success} để
        // VNPay không gọi lại nếu exception không xảy ra)
        return new VnPayIpnResponse("00", "Confirm Success");
    }
}

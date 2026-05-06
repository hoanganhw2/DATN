package com.learning.englishpro.payment.controller;

import com.learning.englishpro.payment.dto.response.VnPayIpnResponse;
import com.learning.englishpro.payment.entity.Order;
import com.learning.englishpro.payment.repository.OrderRepository;
import com.learning.englishpro.payment.service.PaymentWebhookService;
import com.learning.englishpro.payment.util.VNPaySignatureUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Payment Webhook", description = "IPN và Return URL cho cổng thanh toán")
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentWebhookController {

    private final PaymentWebhookService paymentWebhookService;
    private final OrderRepository orderRepository;

    @Value("${payment.vnpay.secret-key:fake-secret-key}")
    private String vnpHashSecret;

    @Operation(summary = "[PUBLIC] VNPay IPN Webhook (GET)")
    @GetMapping("/payments/webhook")
    public ResponseEntity<VnPayIpnResponse> vnpayIpnGet(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(paymentWebhookService.processIpn(params));
    }

    @Operation(summary = "[PUBLIC] VNPay IPN Webhook (POST)")
    @PostMapping("/payments/webhook")
    public ResponseEntity<VnPayIpnResponse> vnpayIpnPost(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(paymentWebhookService.processIpn(params));
    }

    @Operation(summary = "[PUBLIC] VNPay Return URL – xử lý redirect sau thanh toán")
    @GetMapping("/payments/return")
    public ResponseEntity<Map<String, Object>> vnpayReturn(@RequestParam Map<String, String> params) {
        log.info("VNPay Return URL params: {}", params);

        // Delegate hoàn toàn cho processIpn – nó đã tự verify signature bên trong.
        // Không verify lại ở đây để tránh sai lệch do Spring tự decode @RequestParam.
        VnPayIpnResponse ipnResult = paymentWebhookService.processIpn(params);

        String responseCode = params.getOrDefault("vnp_ResponseCode", "99");
        String orderCode    = params.getOrDefault("vnp_TxnRef", "");
        String transactionNo= params.getOrDefault("vnp_TransactionNo", "");
        long   amountVnd    = 0;
        try { amountVnd = Long.parseLong(params.getOrDefault("vnp_Amount", "0")) / 100; }
        catch (Exception ignored) {}

        boolean success = "00".equals(ipnResult.rspCode()) && "00".equals(responseCode);

        Map<String, Object> result = new HashMap<>();
        result.put("valid",         !"97".equals(ipnResult.rspCode())); // 97 = invalid signature
        result.put("success",       success);
        result.put("responseCode",  responseCode);
        result.put("orderCode",     orderCode);
        result.put("transactionNo", transactionNo);
        result.put("amount",        amountVnd);

        return ResponseEntity.ok(result);
    }

    /**
     * Lấy thông tin đơn hàng để frontend hiển thị QR code manual
     */
    @Operation(summary = "[PUBLIC] Lấy thông tin đơn hàng để hiển thị QR")
    @GetMapping("/payments/order-info/{orderCode}")
    public ResponseEntity<Map<String, Object>> getOrderInfo(@PathVariable String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .map(order -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("orderCode", order.getOrderCode());
                    info.put("amount", order.getFinalAmount());
                    info.put("status", order.getStatus().name());
                    info.put("courseTitle", order.getCourse().getTitle());
                    return ResponseEntity.ok(info);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.learning.englishpro.payment.service.impl;

import com.learning.englishpro.payment.entity.Order;
import com.learning.englishpro.payment.service.PaymentGatewayService;
import org.springframework.stereotype.Service;

@Service
public class MockPaymentGatewayServiceImpl implements PaymentGatewayService {

    @Override
    public String createPaymentUrl(Order order) {
        // Tạm thời trả về mock payment URL
        return "https://mock-payment-gateway.com/pay?orderCode=" + order.getOrderCode() + "&amount=" + order.getFinalAmount();
    }
}

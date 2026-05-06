package com.learning.englishpro.payment.service;

import com.learning.englishpro.payment.entity.Order;

public interface PaymentGatewayService {
    String createPaymentUrl(Order order);
}

package com.learning.englishpro.payment.service.impl;

import com.learning.englishpro.payment.entity.Order;
import com.learning.englishpro.payment.service.PaymentGatewayService;
import com.learning.englishpro.payment.util.VNPaySignatureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Primary  // Ưu tiên hơn MockPaymentGatewayServiceImpl
@RequiredArgsConstructor
public class VNPayGatewayServiceImpl implements PaymentGatewayService {

    @Value("${payment.vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${payment.vnpay.secret-key}")
    private String vnpHashSecret;

    @Value("${payment.vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${payment.vnpay.return-url}")
    private String vnpReturnUrl;

    @Override
    public String createPaymentUrl(Order order) {
        String vnpVersion = "2.1.0";
        String vnpCommand = "pay";
        String orderType = "other";

        // Số tiền * 100 (VNPay yêu cầu đơn vị đồng, không có dấu chấm thập phân)
        long amount = order.getFinalAmount().multiply(BigDecimal.valueOf(100)).longValue();

        String vnpTxnRef = order.getOrderCode();
        String vnpOrderInfo = "Thanh toan khoa hoc " + order.getOrderCode();
        String vnpIpAddr = getClientIp();

        String vnpCreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // Hết hạn sau 15 phút
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        cal.add(Calendar.MINUTE, 15);
        String vnpExpireDate = new SimpleDateFormat("yyyyMMddHHmmss").format(cal.getTime());

        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", vnpVersion);
        vnpParams.put("vnp_Command", vnpCommand);
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", vnpTxnRef);
        vnpParams.put("vnp_OrderInfo", vnpOrderInfo);
        vnpParams.put("vnp_OrderType", orderType);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_IpAddr", vnpIpAddr);
        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        // Tạo chuỗi hash
        String secureHash = VNPaySignatureUtil.hashAllFields(vnpParams, vnpHashSecret);
        vnpParams.put("vnp_SecureHash", secureHash);

        // Build URL
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            if (query.length() > 0) query.append("&");
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII));
            query.append("=");
            query.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }

        return vnpPayUrl + "?" + query;
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest req = attrs.getRequest();
            String ip = req.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = req.getRemoteAddr();
            }
            // Lấy IP đầu tiên nếu có nhiều
            if (ip != null && ip.contains(",")) {
                ip = ip.split(",")[0].trim();
            }
            return ip != null ? ip : "127.0.0.1";
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
